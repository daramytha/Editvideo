package com.example.videoeditor.engine

import android.content.Context
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

@UnstableApi
class CustomShaderEffect(
    private val brightness: Float,
    private val contrast: Float,
    private val saturation: Float
) : GlEffect {

    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram {
        return ColorShaderProgram(brightness, contrast, saturation)
    }
}

@UnstableApi
private class ColorShaderProgram(
    private val brightness: Float,
    private val contrast: Float,
    private val saturation: Float
) : GlShaderProgram {
    
    private val program = GlProgram(
        """
        attribute vec4 aFramePosition;
        attribute vec2 aTexCoords;
        varying vec2 vTexCoords;
        void main() {
            gl_Position = aFramePosition;
            vTexCoords = aTexCoords;
        }
        """.trimIndent(),
        """
        precision mediump float;
        varying vec2 vTexCoords;
        uniform sampler2D uTexSampler;
        uniform float uBrightness;
        uniform float uContrast;
        uniform float uSaturation;

        void main() {
            vec4 color = texture2D(uTexSampler, vTexCoords);
            vec3 rgb = color.rgb + uBrightness;
            rgb = (rgb - 0.5) * uContrast + 0.5;
            float luma = dot(rgb, vec3(0.299, 0.587, 0.114));
            rgb = mix(vec3(luma), rgb, uSaturation);
            gl_FragColor = vec4(clamp(rgb, 0.0, 1.0), color.a);
        }
        """.trimIndent()
    )

    private val brightnessLoc = program.getUniformLocation("uBrightness")
    private val contrastLoc = program.getUniformLocation("uContrast")
    private val saturationLoc = program.getUniformLocation("uSaturation")

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long, outputTexId: Int) {
        program.use()
        GlUtil.bindTexture(2, inputTexId)
        program.setSamplerTexIdUniform("uTexSampler", 2, 0)
        program.setFloatUniform(brightnessLoc, brightness)
        program.setFloatUniform(contrastLoc, contrast)
        program.setFloatUniform(saturationLoc, saturation)
        program.bindAttributes()
        GlUtil.drawFrame(outputTexId)
    }

    override fun release() {
        program.release()
    }
}

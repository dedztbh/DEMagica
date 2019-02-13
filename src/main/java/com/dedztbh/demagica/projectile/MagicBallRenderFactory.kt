package com.dedztbh.demagica.projectile

import com.dedztbh.demagica.DEMagica
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.entity.Render
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.registry.IRenderFactory
import org.lwjgl.opengl.GL11


/**
 * Created by DEDZTBH on 2019-02-13.
 * Project DEMagica
 */
class MagicBallRenderFactory<T : Entity> : IRenderFactory<T> {
    override fun createRenderFor(manager: RenderManager): Render<in T> {
        return RenderEMP(manager)
    }

    inner class RenderEMP(manager: RenderManager) : Render<T>(manager) {

        private val model: ModelBase
        val texture = ResourceLocation(DEMagica.MODID, "textures/block/magic2.png")

        init {
            model = ModelEMP()
        }

        override fun getEntityTexture(entity: T): ResourceLocation? {
            return texture
        }

        override fun doRender(entity: T, x: Double, y: Double, z: Double, yaw: Float, partialTick: Float) {
            GL11.glPushMatrix()
            bindTexture(texture)
            GL11.glTranslated(x, y - 1.25, z)
            model.render(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f)
            GL11.glPopMatrix()
        }
    }

    class ModelEMP : ModelBase() {
        internal var Shape1: ModelRenderer
        internal var Shape2: ModelRenderer
        internal var Shape3: ModelRenderer
        internal var Shape4: ModelRenderer

        init {
            textureWidth = 64
            textureHeight = 32

            Shape1 = ModelRenderer(this, 0, 0)
            Shape1.addBox(0f, 0f, 0f, 4, 4, 4)
            Shape1.setRotationPoint(-2f, 19f, -2f)
            Shape1.setTextureSize(64, 32)
            Shape1.mirror = true
            setRotation(Shape1, 0f, 0f, 0f)
            Shape2 = ModelRenderer(this, 0, 0)
            Shape2.addBox(0f, 0f, 0f, 2, 6, 2)
            Shape2.setRotationPoint(-1f, 18f, -1f)
            Shape2.setTextureSize(64, 32)
            Shape2.mirror = true
            setRotation(Shape2, 0f, 0f, 0f)
            Shape3 = ModelRenderer(this, 0, 0)
            Shape3.addBox(0f, 0f, 0f, 6, 2, 2)
            Shape3.setRotationPoint(-3f, 20f, -1f)
            Shape3.setTextureSize(64, 32)
            Shape3.mirror = true
            setRotation(Shape3, 0f, 0f, 0f)
            Shape4 = ModelRenderer(this, 0, 0)
            Shape4.addBox(0f, 0f, 0f, 2, 2, 6)
            Shape4.setRotationPoint(-1f, 20f, -3f)
            Shape4.setTextureSize(64, 32)
            Shape4.mirror = true
            setRotation(Shape4, 0f, 0f, 0f)
        }

        override fun render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
            super.render(entity, f, f1, f2, f3, f4, f5)
            setRotationAngles(f, f1, f2, f3, f4, f5, entity)
            Shape1.render(f5)
            Shape2.render(f5)
            Shape3.render(f5)
            Shape4.render(f5)
        }

        private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
            model.rotateAngleX = x
            model.rotateAngleY = y
            model.rotateAngleZ = z
        }
    }
}
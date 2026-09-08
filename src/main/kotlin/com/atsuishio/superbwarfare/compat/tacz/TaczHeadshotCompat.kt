package com.atsuishio.superbwarfare.compat.tacz

import com.tacz.guns.api.event.common.EntityHurtByGunEvent
import net.minecraft.world.damagesource.DamageSource

/**
 * У TaCZ попадание в голову приходит обычным уроном tacz:bullet, отдельного типа урона нет:
 * признак хедшота есть только в EntityHurtByGunEvent.Pre, который вызывается синхронно
 * прямо перед hurt(). Запоминаем id пули и сверяем его с directEntity урона, поэтому
 * сбрасывать флаг не нужно (Post не приходит, если цель умерла, а id сущностей в рамках
 * сессии не переиспользуются).
 *
 * Класс трогает классы TaCZ, поэтому грузить его можно только под isModLoaded("tacz").
 */
object TaczHeadshotCompat {
    private var headshotBulletId = -1

    fun init() {
        EntityHurtByGunEvent.PRE.register { event ->
            if (event.isHeadShot) headshotBulletId = event.bullet.id
        }
    }

    fun isHeadshot(source: DamageSource) =
        headshotBulletId != -1 && source.directEntity?.id == headshotBulletId
}

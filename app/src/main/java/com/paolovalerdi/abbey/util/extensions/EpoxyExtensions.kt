package com.paolovalerdi.abbey.util.extensions

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.EpoxyModel
import com.paolovalerdi.abbey.adapter.epoxy.ScrollAwareCarouselModelBuilder

inline fun <T> CarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { model -> modelBuilder(model) })
}

inline fun <T> ScrollAwareCarouselModelBuilder.withModelFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { model -> modelBuilder(model) })
}

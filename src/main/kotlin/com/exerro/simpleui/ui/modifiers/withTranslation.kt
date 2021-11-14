import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.ParentContext
import com.exerro.simpleui.ui.ResolvedChild
import com.exerro.simpleui.ui.modifier

@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.withTranslation(
    dx: Pixels = 0.px,
    dy: Pixels = 0.px,
) = modifier<ParentWidth, ParentHeight, ChildWidth, ChildHeight, ChildWidth, ChildHeight> { _, _, _, _, (childWidth, childHeight, draw: DrawContext.() -> Unit) ->
    ResolvedChild(childWidth, childHeight) {
        region.translateBy(dx = dx, dy = dy).draw(draw = draw)
    }
}

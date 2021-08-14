package com.exerro.simpleui

@Undocumented
class LazyRegionList internal constructor(
    private val getNth: (Int) -> Region
) {
    @Undocumented
    operator fun get(index: Int) = getNth(index)

    @Undocumented
    fun map(fn: (Region) -> Region) =
        LazyRegionList { n -> fn(getNth(n)) }

    @Undocumented
    fun drop(count: Int) =
        LazyRegionList { n -> getNth(n + count) }

    @Undocumented
    operator fun component1() = this[0]

    @Undocumented
    operator fun component2() = this[1]

    @Undocumented
    operator fun component3() = this[2]

    @Undocumented
    operator fun component4() = this[3]

    @Undocumented
    operator fun component5() = this[4]
}

fun List<LazyRegionList>.flatten() = LazyRegionList { n ->
    this[n % size][n / size]
}

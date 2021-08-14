package com.exerro.simpleui

@Undocumented
class LazyRegionList internal constructor(
    private val getNth: (Int) -> Region
): Iterable<Region> {
    @Undocumented
    operator fun get(index: Int) = cache.computeIfAbsent(index, getNth)

    @Undocumented
    fun map(fn: (Region) -> Region) =
        LazyRegionList { n -> fn(get(n)) }

    @Undocumented
    fun flatMap(fn: (Region) -> List<Region>): LazyRegionList = LazyRegionList { n ->
        val r = fn(get(0))
        if (n < r.size) r[n]
        else drop(1).flatMap(fn)[n - r.size]
    }

    @Undocumented
    fun drop(count: Int) =
        LazyRegionList { n -> get(n + count) }

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

    override fun iterator() = object: Iterator<Region> {
        private var index = 0
        override fun hasNext() = true
        override fun next() = get(index++)
    }

    @Undocumented
    private val cache: MutableMap<Int, Region> = mutableMapOf()
}

fun List<LazyRegionList>.flatten() = LazyRegionList { n ->
    this[n % size][n / size]
}

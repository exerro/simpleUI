package com.exerro.simpleui

@Undocumented
class LazyRegionList internal constructor(
    private val getNth: (Int) -> Region
): Iterable<Region> {
    @Undocumented
    operator fun get(index: Int) =
        cache.computeIfAbsent(index, getNth)

    @Undocumented
    operator fun get(from: Int, to: Int) =
        (from until to).map { get(it) }

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

    operator fun component1() = this[0]
    operator fun component2() = this[1]
    operator fun component3() = this[2]
    operator fun component4() = this[3]
    operator fun component5() = this[4]
    operator fun component6() = this[5]
    operator fun component7() = this[6]
    operator fun component8() = this[7]
    operator fun component9() = this[8]

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

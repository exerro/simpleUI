package com.exerro.simpleui

/** An infinite list of [Region]s, evaluated lazily. */
class LazyRegionList internal constructor(
    private val getNth: (Int) -> Region
): Iterable<Region> {
    /** Return the [index]th region. */
    operator fun get(index: Int) =
        cache.computeIfAbsent(index, getNth)

    /** Return a list of regions, including the [from]th region up until (and
     *  not including) the [to]th region. E.g. get(1, 3) will be a list of
     *  [get(1), get(2)]. */
    operator fun get(from: Int, to: Int) =
        (from until to).map { get(it) }

    /** Map regions using [fn]. */
    fun map(fn: (Region) -> Region) =
        LazyRegionList { n -> fn(get(n)) }

    /** Map each [Region] in this list to a list of regions, and flatten the
     *  result into a new [LazyRegionList]. */
    fun flatMap(fn: (Region) -> List<Region>): LazyRegionList = LazyRegionList { n ->
        val r = fn(get(0))
        if (n < r.size) r[n]
        else drop(1).flatMap(fn)[n - r.size]
    }

    /** Ignore the first [count] regions from this list. */
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

    /** A private cache of regions to avoid re-calculating regions. */
    private val cache: MutableMap<Int, Region> = mutableMapOf()
}

/** Flatten a list of [LazyRegionList]s into a single [LazyRegionList]. */
fun List<LazyRegionList>.flatten() = LazyRegionList { n ->
    this[n % size][n / size]
}

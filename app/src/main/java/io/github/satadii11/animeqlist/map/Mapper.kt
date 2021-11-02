package io.github.satadii11.animeqlist.map

interface Mapper<From, To> {
    fun map(from: From): To
}
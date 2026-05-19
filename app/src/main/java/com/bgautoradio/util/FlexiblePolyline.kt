package com.bgautoradio.util

import org.osmdroid.util.GeoPoint
import kotlin.math.pow

/**
 * Decoder for HERE Flexible Polyline encoding.
 * Spec: https://github.com/heremaps/flexible-polyline
 */
object FlexiblePolyline {

    private val TABLE = IntArray(256) { -1 }.also { arr ->
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
            .forEachIndexed { i, c -> arr[c.code] = i }
    }

    fun decode(encoded: String): List<GeoPoint> {
        if (encoded.length < 2) return emptyList()
        var pos = 0

        fun nextChar() = TABLE[encoded[pos++].code]

        val version = nextChar()
        if (version != 1) return emptyList()

        val precByte  = nextChar()
        val precision = precByte and 0xF
        val factor    = 10.0.pow(precision)

        fun decodeValue(): Long {
            var shift = 0
            var value = 0L
            var delta: Int
            do {
                delta  = TABLE[encoded[pos++].code]
                value  = value or ((delta and 0x1F).toLong() shl shift)
                shift += 5
            } while (delta >= 0x20)
            // zigzag decode
            return (value ushr 1) xor -(value and 1L)
        }

        val result = mutableListOf<GeoPoint>()
        var lat = 0L
        var lon = 0L
        while (pos < encoded.length) {
            lat += decodeValue()
            if (pos >= encoded.length) break
            lon += decodeValue()
            result.add(GeoPoint(lat / factor, lon / factor))
        }
        return result
    }
}

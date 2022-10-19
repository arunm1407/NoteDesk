package com.example.version2.domain.model

import java.io.Serializable


sealed class Color(var color: String):Serializable {


    companion object {
        const val COLOR_1 = "#FFFFFF"
        const val COLOR_2 = "#fcada8"
        const val COLOR_3 = "#f19f71"
        const val COLOR_4 = "#EAC78C"
        const val COLOR_5 = "#b4ddd4"
        const val COLOR_6 = "#d3bed7"
        const val COLOR_7 = "#ECB680"
        const val COLOR_8 = "#81DDD3"
        const val COLOR_9 = "#A4ADE4"
        const val COLOR_10 = "#F57777"


        fun String.getValues():Color {
           return when (this) {
                COLOR_1 -> Color1
                COLOR_2 -> Color2
                COLOR_3 -> Color3
                COLOR_4 -> Color4
                COLOR_5 -> Color5
                COLOR_6 -> Color6
                COLOR_7 -> Color7
                COLOR_8 -> Color8
                COLOR_9 -> Color9
                COLOR_10 -> Color10
                else -> Color1
            }

        }
    }

    object Color1 : Color(COLOR_1)
    object Color2 : Color(COLOR_2)
    object Color3 : Color(COLOR_3)
    object Color4 : Color(COLOR_4)
    object Color5 : Color(COLOR_5)
    object Color6 : Color(COLOR_6)
    object Color7 : Color(COLOR_7)
    object Color8 : Color(COLOR_8)
    object Color9 : Color(COLOR_9)
    object Color10 : Color(COLOR_10)

}


/*

domain
domain - db entity
database
database interaction - dao
repo
ui

 */
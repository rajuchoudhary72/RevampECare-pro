package com.app.ecarepro.utils.listener

interface ItemListener<T> {

    fun onItemClick(t : T,pos: Int,boolean: Boolean)

}
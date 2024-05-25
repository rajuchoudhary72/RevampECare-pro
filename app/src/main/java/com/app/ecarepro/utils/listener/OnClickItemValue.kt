package com.app.ecarepro.utils.listener

interface OnClickItemValue<T> {

    fun onItemClick(t : T,pos: Int,action: Int )

}
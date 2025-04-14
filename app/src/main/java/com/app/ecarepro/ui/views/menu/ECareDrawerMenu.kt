package com.app.ecarepro.ui.views.menu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.databinding.LayoutEcareDrawerMenuBinding
import com.app.ecarepro.drawerChildChildItem
import com.app.ecarepro.drawerChildItem
import com.app.ecarepro.drawerItem
import com.google.android.material.navigation.NavigationView

class ECareDrawerMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    private val binding: LayoutEcareDrawerMenuBinding

    private val menuItems = mutableListOf<DrawerMenu>()

    private var expandedMenuId: Int = -1

    private var listener: EcareDrawerClickListener? = null

    init {
        // Inflate the layout using ViewBinding
        val inflater = LayoutInflater.from(context)
        binding = LayoutEcareDrawerMenuBinding.inflate(inflater, this, true)

        setupRecyclerView()
        // Optionally, setup header/footer here if needed
    }

    private fun setupRecyclerView() {
        // Configure your EpoxyRecyclerView if needed
        binding.recyclerViewNavView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)

            withModels {
                menuItems.forEach { parentMenu ->
                    drawerItem {
                        id(parentMenu.menuID)
                        title(parentMenu.title)
                        icon(parentMenu.icon)
                        hasChildMenu(parentMenu.childMenus.isNullOrEmpty().not())
                        clickListener { _ ->
                            if (parentMenu.childMenus.isNullOrEmpty().not()) {
                                expandedMenuId = if (expandedMenuId == parentMenu.menuID) {
                                    -1
                                } else {
                                    parentMenu.menuID
                                }
                                this@withModels.requestModelBuild()
                            } else {
                                listener?.onClickMenu(parentMenu.menuID, refId = "Menu")
                            }
                        }
                    }

                    if (expandedMenuId == parentMenu.menuID) {
                        parentMenu.childMenus?.forEach { menu ->
                            drawerChildItem {
                                id(parentMenu.menuID, menu.menuID)
                                title(menu.title)
                                icon(menu.icon)
                                clickListener { _ ->
                                    listener?.onClickMenu(
                                        menuId = parentMenu.menuID,
                                        childMenuId = menu.chMenuID,
                                        refId = "Menu"
                                    )
                                }
                            }

                            menu.childMenus?.forEach { childChildMenu ->
                                drawerChildChildItem {
                                    id(parentMenu.menuID, childChildMenu.menuID)
                                    title(childChildMenu.title)
                                    icon(childChildMenu.icon)
                                    clickListener { _ ->
                                        listener?.onClickMenu(
                                            menuId = parentMenu.menuID,
                                            childMenuId = menu.chMenuID,
                                            childChildMenuId = childChildMenu.sbChMenuID,
                                            refId = "Menu"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setMenuItems(items: List<DrawerMenu>) {
        menuItems.clear()
        menuItems.addAll(items)
        binding.recyclerViewNavView.requestModelBuild()
    }

    fun setListener(listener: EcareDrawerClickListener) {
        this.listener = listener
    }

    fun removeListener() {
        listener = null
    }

    interface EcareDrawerClickListener {
        fun onClickHeader()
        fun onClickMenu(
            menuId: Int,
            childMenuId: Int? = null,
            childChildMenuId: Int? = null,
            refId: String? = null
        )

        fun onClickFooter()
    }

}

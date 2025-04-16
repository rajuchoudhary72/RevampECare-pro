package com.app.ecarepro.ui.views.menu

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ecarepro.BuildConfig
import com.app.ecarepro.R
import com.app.ecarepro.databinding.LayoutEcareDrawerMenuBinding
import com.app.ecarepro.drawerChildChildItem
import com.app.ecarepro.drawerChildItem
import com.app.ecarepro.drawerItem
import com.app.ecarepro.utils.imageUrl
import com.google.android.material.navigation.NavigationView

class ECareDrawerMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    private val binding: LayoutEcareDrawerMenuBinding

    private val menuItems = mutableListOf<DrawerMenu>()

    private var menuHeader: MenuHeader? = null

    private var expandedMenuId: Int = -1

    private var listener: EcareDrawerClickListener? = null

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutEcareDrawerMenuBinding.inflate(inflater, this, true)
        setUiHeaderUi()
        setupRecyclerView()
        setUpFooter()
    }


    private fun setupRecyclerView() {
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

    fun setHeaderMenu(header: MenuHeader) {
        this.menuHeader = header
        setUiHeaderUi()
    }

    private fun setUiHeaderUi() {
        binding.itemDrawerHeader.apply {
            imgUserAvatar.imageUrl(
                menuHeader?.userPhoto,
                ContextCompat.getDrawable(imgUserAvatar.context, R.drawable.img_school_placeholder)
            )
            txtUserName.text = menuHeader?.fullName
            txtUserType.text = menuHeader?.otherInfo
            groupUser.setOnClickListener {
                listener?.onClickHeader()
            }
        }
    }


    private fun setUpFooter() {
        binding.itemDrawerFooter.apply {
            txtAppVersion.text = "App Version: ${BuildConfig.VERSION_NAME}"
            btnLogout.setOnClickListener {
                listener?.onClickFooter()
            }
        }
    }


    fun setMenuItems(items: List<DrawerMenu>) {
        menuItems.clear()
        menuItems.addAll(items)
        binding.recyclerViewNavView.requestModelBuild()
    }

    fun requestMenuModelBuild(){
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

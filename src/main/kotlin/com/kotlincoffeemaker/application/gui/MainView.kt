package com.kotlincoffeemaker.application.gui

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.h3
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.router.Route

@Route("view")
class MainView : KComposite() {
    val root = ui {
        verticalLayout {
            h3("Main view")
        }
    }
}
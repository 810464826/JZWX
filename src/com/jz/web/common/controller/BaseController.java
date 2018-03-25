package com.jz.web.common.controller;

import com.jfinal.core.Controller;
import com.jz.web.common.render.BslRender;

public class BaseController extends Controller {

	public void renderBsl(String view) {
		this.render(new BslRender(view));
	}
}

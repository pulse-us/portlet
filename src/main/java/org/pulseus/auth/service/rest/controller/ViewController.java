package org.pulseus.auth.service.rest.controller;

import java.io.IOException;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("view")
public class ViewController {

	@RenderMapping()
	public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws IOException {

		ModelAndView modelAndView = new ModelAndView();

		modelAndView.setViewName("view");

		return modelAndView;
	}
	
}
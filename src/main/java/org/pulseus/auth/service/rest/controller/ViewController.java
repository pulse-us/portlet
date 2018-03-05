package org.pulseus.auth.service.rest.controller;

import java.io.IOException;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

/**
 * View controller for basic rendering.
 * @author alarned
 *
 */
@Controller
@RequestMapping("view")
public class ViewController {

    private Configuration configuration = ConfigurationFactoryUtil
            .getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");

    /**
     * Handles requests for rendering.
     * @param request request to handle
     * @param response response to handle
     * @return model and view object
     * @throws IOException if needed
     */
    @RenderMapping()
    public ModelAndView handleRenderRequest(final RenderRequest request, final RenderResponse response)
            throws IOException {

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("view");
        request.setAttribute("portalUrl", configuration.get("portalUrl"));

        return modelAndView;
    }

}

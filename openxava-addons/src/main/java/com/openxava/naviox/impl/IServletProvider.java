package com.openxava.naviox.impl;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * tmr
 * 
 * @since 7.0 
 * @author Javier Paniza
 */

public interface IServletProvider {
	
	void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}

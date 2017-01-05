package com.kyleduo.rabbits;

/**
 * Enter point of routing.
 *
 * Created by kyle on 2016/12/7.
 */

interface IRouter {
	String METHOD_ROUTE = "route";
	String METHOD_OBTAIN = "obtain";

	Object route(String page);

	Object obtain(String page);
}

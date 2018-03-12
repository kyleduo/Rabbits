# Rabbits

[中文](./README_CN.md)

**NOTICE** Rabbits has a lot of changes after version 1.0.0. If you’re using Rabbits, read wiki before update.

### Changes in version 1.0.0

I released Rabbits last year and have been using it since then. Everything works fine and the “**From-To-Start**” pattern make me almost forget the `new Intent()` stuff. Rabbits simplify the navigation between pages hosted by Activities and Fragments and, with URI as the protocol, navigation between web pages and native pages becomes easier. 

Even though the usage is easier and intuitive, I found some which can be improved. There are 3 points which are more important.

1. `mappings.json`. When I first design Rabbits, I design by separating App client from the backend. But in practice, it’s much reasonable and flexible to let backend control the behavior of App.
2. `Interceptor`. Interceptor is important when you need to control the process of navigation, adding some params or redirecting to another page. It looks silly that you need check whether the url is the one you want when using Interceptor. The condition of url should be bound to the interceptor so only the urls you want can enter the process method, which is that logical.
3. `obtain`. It’s not consistent when you try to obtain a Fragment object using Rabbits since the towering `obtain` API.

Therefore,  I’ve rethought the design of Rabbits and browsed other router libraries to learn what is really needed of a router. However, my decision is to keep Rabbits simple and make it’s usage much more easier. Rabbits 1.0.0 focus just one thing, navigation through pages.

Since I redesigned Rabbits from the bottom. Almost all of the APIs have changed. I’m going to list some significant and obvious change below, the others about the new version can be found in wiki.

1. `mappings.json` has been removed. You need to annotated name on every page’s class. `@Page()` has changed and you can use it like this: `@Page("/page/path")`.
2. Initialization API has changed a lot but the new API becomes more fluent and you can finish the initialization by a single link of invocations.
3. `P` class is still there. Since `mappings.json` is gone so Rabbits generate P using url path specified in `@Page` or you can provide a `alias` field to name that page a friendly name. Field generated from a path may looks like this: **P_PAGE_PATH**. Yes, there is a prefix, `P_`.
4. The execution chain has been redesigned and interceptors are recommended set during initialization phase.

### Use with Gradle

```groovy
dependencies {
    implementation "com.kyleduo.rabbits:rabbits:1.0.0"
    annotationProcessor "com.kyleduo.rabbits:compiler:1.0.0"
}
```

### From-To-Start pattern

The From-To-Start pattern is quite simple and intuitive since navigation is just from on page to another.

```java
// MainActivity.java
Rabbit.from(this).to(P.P_TEST).start();
```
### ProGuard rules

Since Rabbits generate routing table in compile phase and no reflection during navigation, you just need add this line in your ProGuard rules file.
```
-keep class com.kyleduo.rabbits.Router { *; }
```
### Migration

If you have been using Rabbits before 1.0.0, read the wiki first before update.

### Thanks

[OKHttp](https://github.com/square/okhttp): I learnt how to implement interceptor chain from OKHttp.

[ARouter](https://github.com/alibaba/ARouter): The idea`flags` is from ARouter.


License
---

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

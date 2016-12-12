# Rabbits

**This project is just Under developing.**

This library is a **ROUTER** implementation for Android apps with configurable route mapping support.

### Basic Features

1. Navigation using **URI**.
2. **Simplified** uri support.
3. **REST** uri support.
4. **Bundle** and **Intent flags** support.
5. Configurable route mappings, in **JSON syntax**.
6. Update mappings use JSON or **File**.
7. **NotFoundHandler**.(the last ditch)


### Extension Feature

1. **Global/Local interceptor** support.
2. **Custom Navigator** support. Basic extension is for navigation between fragments.
3. **Fragment** annotations support.\*

> \* Rabbits just support annotate a Fragment class but not handle the navigation details. Whitch means you have a chance to implements your own navigation between fragments while Rabbits just provides a protocal of the operation.



### Glossary

| Glossary  | Meanings                                 |
| :-------- | :--------------------------------------- |
| Rabbit    | Entry class to use Rabbits.              |
| Page      | Page representing an Activity or a Fragment witch will be a unit navigating to. |
| Page Name | Name of a page.  No matter case but using upper case is recommanded. |
| Mappings  | Where mapping stored. A mapping is from a uri pattern to a page name. |



### Basic Usages

#### Start activity using Rabbits

```java
Rabbit.from(MainActivity.this)
	.to("demo://rabbits.kyleduo.com/test")
	.start();
```

> Uri can also be simplified to these format if you want:
>
> * "demo:///test"
> * "/test"
> * "test"

#### Initialization & Setup

To ensure mappings is full loaded when enter content, you need initialize and setup Rabbits properly before using it.

Custom Application class and write these line in the onCreate method.

```java
Rabbit.init("demo", "rabbits.kyleduo.com");
Rabbit.setup(this);
```

> It is not that necessary that the operation is in Application's onCreate method. Just make sure it finished before user can interract with your app.

The 1st line will initalize Rabbit using applicaton scheme and host. The 2nd line let Rabbit to setup itself using mappings. The setup operation contains I/O work, if you very concern the launch time of your app, you can put the setup operation into work thread using this method.

```java
Rabbit.asyncSetup(this, new Runnable() {
    public void run() {}
});
```

`asyncSetup`method will put the I/O operation into work thread so it would not block the ui thread. But you need handle the finish event to notify user that they can navigate through your app now.

#### Prepare Pages for Navigation

Rabbits uses Annotations to name a page.

```java
@Page(name = "TEST")
public class TestActivity extends AppCompatActivity {
  
}
```

This is a simplest annotion witch named an activity with `TEST`. So a uri matches `TEST` will start this activity.

Rabbits also support Fragment annotations so if you have a page in Fragment, you can annotated like this:

```java
@Page(name = "TEST", type = PageType.FRAGMENT)
public class TestFragment extends Fragment {
  
}
```

This will cause a different navigator result. More details will be added later.

#### Arguments

The first example:

```java
Rabbit.from(MainActivity.this)
	.to("demo://rabbits.kyleduo.com/test")
	.start();
```

The `to(String)` method will create a `AbstractNavigator` class, witch will execute the navigation operation. After you get the object, you can put your extra params into it like this.

```java
Rabbit.from(MainActivity.this)
	.to("demo://rabbits.kyleduo.com/test")
  	.putString("foo", "bar")
	.start();
```

There are a bunch of putXxx methods. Each fo them has the same name to one from Bundle, so it's sure simple for you to use.

#### Flags

Like the arguments part, flags can be set to navigator easily. These are four methods work around with flags.

```java
addIntentFlags(int);
setIntentFlags(int);
newTask();
clearTop();
```

This methods work just like they are named.

#### Mappings json format

```json
{
  "version": 1,
  "force_override": 1,
  "mappings": {
    "demo://rabbits.kyleduo.com/test": "TEST",
    "demo://rabbits.kyleduo.com/test/{Testing}": "TEST",
    "demo://rabbits.kyleduo.com": "MAIN"
  }
}
```

**`mappings`** is the key part, witch contains mapping from **URI** to **PAGE NAME**.

> If `force_override` is set to `1`, version control would be disabled and force override local mapping using this mappings.

Basic uri format should like this:

```
[scheme]://[domain]/[path]/[path]
```

For example, `demo://rabbits.kyleduo.com/test` is a valid uri.

> Uri should not end up with `/` and should not contains queries.

If a uri need support REST format, it may be changed to this one.

```
demo://rabbits.kyleduo.com/test/{id:l}
```

This means it requires a param of `long` type and named `id`. A REST param in braces consists of 2 parts, `name` and `type` , seprated with a colon.

Other supported types are **`i`** for `int`, **`d`** for `double`, **`s`** for `String`, **`b`** for `boolean`. If none type specificed, the value would be treated as a String.

> A valid uri matching this uri pattern has these versions.
>
> * "demo://rabbits.kyleduo.com/test/1"
> * "demo:///test/1"
> * "/test/1"
> * "test/1"

#### Interceptor

Rabbits support both global and local interceptors. Glocal interceptors should be added to `Rabbit` class using static method.

```java
Rabbit.addGlobalInterceptor(new INavigationInterceptor() {
    @Override
    public boolean intercept(Uri uri, Object from, Object to, String page, int intentFlags, Bundle extras) {
        if (uri.getPath().equals("/intercept/dump")) {
			Rabbit.from(from)
          		.to("/dump")
          		.mergeExtras(extras)
          		.start();
          	return true;
      	}
      	return false;
    }
});
```

This interceptor will be checked every times while **local interceptors** only checked in contextual navigators. Local interceptors usage like this.

```java
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
	INavigationInterceptor webInterceptor = new INavigationInterceptor() {
		@Override
		public boolean intercept(Uri uri, Object from, Object to, String page, int intentFlags, Bundle extras) {
			if (uri.getPath().equals("/tobeintercepted")) {
				Rabbit.from(WebFragment.this)
						.to("demo://rabbits.kyleduo.com/test")
						.mergeExtras(extras)
						.clearTop()
						.start();
				return true;
			}
			return false;
		}
	};

	boolean ret = Rabbit.from(WebFragment.this)
			.addInterceptor(webInterceptor)
			.tryTo(url)
			.start();
	return ret || super.shouldOverrideUrlLoading(view, url);
}
```

This is also an example of using Rabbits in `WebViewClient` for intercepting urls. This example uses `tryTo(String)` to check whether there is a native page matches this url. If not, a `MuteNavigator` would return and `ret` should be **`flase`**.

Different from `tryTo(String)` , normal `to(String)` method will send the origin url to a `AbstractPageNotFoundHandler` instance to find the last operation.

#### AbstractPageNotFoundHandler

This is subclass of `AbstractNavigator` whitch means `AbstractPageNotFoundHandler` also has the ability of navigation.

A `AbstractPageNotFoundHandler` object is create by `INavigatorFactory` object witch sent to `Rabbit.init()` method.

```java
Rabbit.init("demo", "rabbits.kyleduo.com", new DemoNavigatorFactory());
```

For example, there is possibly a Handler witch fallback page navigation to web page loading in your application.

```java
public class DemoNotFoundHandler extends AbstractPageNotFoundHandler {

	public DemoNotFoundHandler(Object from, Uri uri, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		super(from, uri, tag, flags, extras, interceptors);
	}

	@Override
	public boolean start() {
		String httpUrl = mUri.buildUpon().scheme("http").build().toString();
		Rabbit.from(mFrom)
				.to("/web")
				.putString("url", httpUrl)
				.start();
		return true;
	}

	@Override
	public boolean startForResult(int requestCode) {
		String httpUrl = mUri.buildUpon().scheme("http").build().toString();
		Rabbit.from(mFrom)
				.to("/web")
				.putString("url", httpUrl)
				.startForResult(requestCode);
		return true;
	}

	@Override
	public Object obtain() {
		String httpUrl = mUri.buildUpon().scheme("http").build().toString();
		return Rabbit.from(mFrom)
				.obtain("/web")
				.putString("url", httpUrl)
				.obtain();
	}
}
```

> This code snippet from demo application handle page not found by change the uri scheme to http witch is a web scheme and dispatch it to a built-in web page —— you can also wake up system browser app using intent.

#### Custom Navigator

Last but not least, to support custom Fragment navigation or other custom operations, Rabbits support inject custom `Navigator` by create a custom `INavigatorFactory` instance, as mentioned before.

This is a major flexible part of Rabbits design, not just finish the navigation job but give you a chance to handle the key part —— **Navigation** —— and without care about the tough parts: mappings, params, flags and so on.




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

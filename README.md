# Rabbits

Rabbits aims to unify the navigation through out both native app page and web page on Android. Rabbits providing navigation mechanism using **URI**, a common and well known protocol.



You can get these benefits after integrating Rabbits to you project:

1. **Uniform navigation** through out the whole project.
2. Annotation processing in **compile phase**.
3. **ProGuard** support.
4. Both **Activity** and **Fragment** annotations Support.
5. **Sub-Module** support
6. Managing all router rules by just one **JSON** file.
7. Simple **invocation**, easy to **understand**, convenient for **extension**.
8. Transfer parameters through **url query** or **REST** segments or a bench of  **`putExtra`** methods.
9. Update router mappings in **runtime**.
10. Many other usability designs.



### Download

```groovy
dependencies {
    compile "com.kyleduo.rabbits:rabbits:1.0.0"
    annotationProcessor "com.kyleduo.rabbits:compiler:1.0.0"
}
```



### Proguard rules

```
-keep class com.kyleduo.rabbits.Router { *; }
-keep class com.kyleduo.rabbits.IRouter { *; }
```




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

Foldable Layout
=================
This code is a showcase of a foldable animation I created for Worldline. The code is fully written
with java APIs from the Android SDK, without the use of any external library. This code is a demo and not a library.
The foldable layout is a layout (strictly a RelativeLayout), which can have two child layouts, one for the cover, and
one for the details. The foldable layout takes care of the folding animation between those two layouts.

In the demo the foldable layout is used in a RecyclerView, to show that it can manage well its size even during animation.

![](https://raw.githubusercontent.com/worldline/FoldableLayout/dev/screenshots/demo.gif)
<a href="http://www.youtube.com/watch?feature=player_embedded&v=XOAcNW82dl8
" target="_blank"><img src="http://img.youtube.com/vi/XOAcNW82dl8/0.jpg"
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>


Under the hood
==============
Work in progress.

Is a library available ?
========================
The library is available as is. No support guarantied. This is more a showcase, and if you want to use it, I greatly recommend you to understand the code from the `foldablelayout` and adapt it
to your usage.

 - Ensure you can pull artifacts from Maven Central :

   ```gradle
   repositories {
     mavenCentral()
   }
   ```

 - And add to your module gradle file :

   ```gradle   
   dependencies {
     compile 'com.vincentbrison.openlibraries.android:foldablelayout:0.0.1@aar'
   }
   ```   

License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

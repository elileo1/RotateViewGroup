# RotateViewGroup

Carousel effect with sticky sliding, smooth zooming, selection and click events.

<img src="https://raw.githubusercontent.com/fanrunqi/RotateViewGroup/master/app/1.png" width = "540" height = "275"  />

gif：

<img src="https://raw.githubusercontent.com/fanrunqi/RotateViewGroup/master/app/2.gif" width = "704" height = "459"  />

# Usage

## dependency

>copy the file [RotateViewGroup.java](https://github.com/fanrunqi/RotateViewGroup/tree/master/app/src/main/java/cn/leo/rotateviewgroup) in your project.

## layout

>set like this

```
<cn.leo.rotateviewgroup.RotateViewGroup
        android:id="@+id/rotateViewGroup"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ImageView
            android:src="@drawable/item"
            android:layout_width="130dp"
            android:layout_height="130dp"/>
        <ImageView
            android:src="@drawable/item2"
            android:layout_width="130dp"
            android:layout_height="130dp"/>
        <ImageView
            android:src="@drawable/item3"
            android:layout_width="130dp"
            android:layout_height="130dp"/>
        <ImageView android:src="@drawable/item4"
            android:layout_width="130dp"
            android:layout_height="130dp" />

    </cn.leo.rotateviewgroup.RotateViewGroup>
```

## activity

>set Callback event

```
RotateViewGroup rotateViewGroup = findViewById(R.id.rotateViewGroup);
        //当前选择view回调
        rotateViewGroup.setSelectListener(new RotateViewGroup.rotateViewSelectListener() {
            @Override
            public void selectViewNo(int i, View v) {
                toast("select:"+i);
            }
        });
        //点击view回调
        rotateViewGroup.setClickListener(new RotateViewGroup.rotateViewClickListener() {
            @Override
            public void clickViewNo(int i, View v) {
                toast("click:"+i);
            }
        });
```
## manifests

>if need add this in your acticity

```
android:screenOrientation="landscape"
```

**simple life , easy code**.

# License
> Copyright 2018 fanrunqi

> Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  >  http://www.apache.org/licenses/LICENSE-2.0

> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

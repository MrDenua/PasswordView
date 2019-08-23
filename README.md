## PasswordView 一个输入密码及验证码的 View

[PasswordView.java](https://github.com/MrDenua/PasswordView/blob/master/app/src/main/java/red/djh/passwordview/PasswordView.java)

## Screenshot

![screenshot](https://raw.githubusercontent.com/MrDenua/PasswordView/master/screenshot/a.png)

## Usage

### Attributes

    <declare-styleable name="PasswordView">
        <attr name="backgroundColor" format="color" />
        <attr name="length" format="integer" />
        <attr name="textSize" format="dimension" />
        <attr name="inputType" format="flags">
            <flag name="worlds" value="0x01" />
            <flag name="number" value="0x10" />
        </attr>
        <attr name="borderRadius" format="dimension" />
        <attr name="maskSize" format="dimension" />
        <attr name="maskColor" format="color" />
        <attr name="borderWidth" format="dimension" />
        <attr name="showPassword" format="boolean" />
        <attr name="borderColor" format="color" />
    </declare-styleable>
        
### 设置长度

    passwordView.setLength(6);
    
### 设置输入完毕监听

    passwordView.setOnInputFinishListener(new PasswordView.OnInputFinishListener() {
                @Override
                public void onInputFinish(String[] password) {
                    
                }
            });

### 清空

    passwordView.clear();


var onoff = true//根据此布尔值判断当前为注册状态还是登录状态
var confirm = document.getElementsByClassName("confirm")[0]
var user = document.getElementById("username")
var passwd = document.getElementById("password")
var con_pass = document.getElementById("confirm-passwd")

//自动居中title
var name_c = document.getElementById("title")
name = name_c.innerHTML.split("")
name_c.innerHTML = ""
for (i = 0; i < name.length; i++)
    if (name[i] != ",")
        name_c.innerHTML += "<i>" + name[i] + "</i>"
// //引用hint()在最上方弹出提示
// function hint() {
//     let hit = document.getElementById("hint")
//     hit.style.display = "block"
//     setTimeout("hit.style.opacity = 1", 0)
//     setTimeout("hit.style.opacity = 0", 2000)
//     setTimeout('hit.style.display = "none"', 3000)
// }
//注册按钮
function signin() {
    //回调函数
    let hit = document.getElementById("hint").getElementsByTagName("p")[0]
    var submit = function (callback) {
        console.log("提交注册表单!")
        let request = new XMLHttpRequest()
        let url = "http://localhost:8080/user/regist";
        request.open("POST", url, true)
        request.setRequestHeader('Content-type','application/x-www-form-urlencoded; charset=utf-8');
        let formData = {};
        formData.userName =  user.value
        formData.password = passwd.value
        console.log(JSON.stringify(formData))
        let callback2 = function () {
            if (this.readyState == 4) {
                console.log("回调成功")
                callback.call(this, this.response)
            }
        }
        request.send(formData)

    }
    let status = document.getElementById("status").getElementsByTagName("i")
    if (onoff) {
        confirm.style.height = 51 + "px"
        status[0].style.top = 35 + "px"
        status[1].style.top = 0
        onoff = !onoff
    } else {
        if (!/^[A-Za-z0-9]+$/.test(user.value))
            hit.innerHTML = "账号只能为英文和数字"
        else if (passwd.value != con_pass.value)
            hit.innerHTML = "两次密码不相等"
        else if (passwd.value = con_pass.value) {
            submit(function (res) {
                if (res == "exist")
                    hit.innerHTML = "该账号已存在"
                else if (res == true) {
                    hit.innerHTML = "账号注册成功，两秒后自动刷新页面"
                    setTimeout("window.location.reload()", 2000)
                } else if (res == false)
                    hit.innerHTML = "账号注册失败"
            })
        }
        //hint()
    }
}
function verSubmit() {

    if (!onoff) {
        let status = document.getElementById("status").getElementsByTagName("i")
        confirm.style.height = 0
        status[0].style.top = 0
        status[1].style.top = 35 + "px"
        onoff = !onoff
    }
    else {
        var uname = $.trim($("#username").val());
        var passwd = $.trim($("#password").val());
        console.log("uname:" + uname);
        console.log("passwd" + passwd);
        if (uname == "" || uname == null || uname == "您的用户名") {

            alert("用户名不能为空,请输入用户名!");
            document.getElementById("username").focus();
            return false;
        }
        if (passwd == "" || passwd == null || passwd == "登录密码") {
            alert("密码不能为空,请输入密码!");
            $("#password").focus();
            return false;
        }

        $("#loginForm").submit();
    }
}

function reloadImg() {
    $("#validateImg").attr("src", $("#validateImg").attr("src").split("?")[0] + "?" + new Date().getTime())
}

function tologin() {
    window.location.href = "/";
}
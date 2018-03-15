$(document).ready(function(){
    var userAddUrl = "http://localhost:8080/addUser.do";

    $("#sure").click(function(){
        var user = $("#user").val();
        var name = $("#name").val();
        var wage = $("#wage").val();
        var password = $("#password").val();
        var pass1 = $("#password1").val();

        if (user == null || name == null || wage == null || password == null || pass1 == null || user == "" || name == "" || wage == "" || password == ""|| pass1 == "" ) {
            alert("输入不能为空");
            return;
        }
        if(user.length!==7){
            alert("警号必须为7位！");
            return;
        }
        if(pass!==pass1){
            alert("两次输入密码不一致！");
            return;
        }
        password= hex_md5(password);

        $.ajax({
            type: 'POST',
            url: userAddUrl,
            data: {
                serialNumber:user,
                name:name,
                salary:wage,
                password:password
            },
            success: function (data) {
                console.info(data);
                if(data.result==="0"){
                    alert("添加成功！");
                    $(location).attr("href","userList.html");
                }
                else{
                    alert(data.message);
                }
            },
            dataType: "json"
        });

    });

    $("#back").click(function(){
        $(location).attr("href","userList.html");
    });

});
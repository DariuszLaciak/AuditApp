$(function () {
    $("#loginPanel").submit(function(event){
        event.preventDefault();
        var login = $("#user_id").val();
        var password = $("#user_password").val();
        $.ajax({
            url: "Login",
            method: "POST",
            dataType: "json",
            async: false,
            data: {
                login: login,
                password: password
            },
            success: function(data){
                if(data.success){
                    $("#loginPanel").remove();
                    $("#header").append(data.data);
                }
                else {

                }
            }
        });
    });
    $("#registerUser").click(function(){
        alert("formularz rejestracji");
    });
});

function logUserIn(){
}
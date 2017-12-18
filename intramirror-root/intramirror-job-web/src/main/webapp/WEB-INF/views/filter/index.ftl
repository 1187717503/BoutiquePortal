<#assign base=request.contextPath />
<!doctype html>
<html>
<head>
    <script src="/js/jquery-3.2.1.min.js"></script>
</head>
<body>
<form id="myForm" action="/product/filter/submit" method="post">

    <p>请选择需要过滤的season_code和brand</p>
    <p>
        <select id="selUser">
        <#list vendorList as v>
            <option value="${v.vendor_id}"
            <#if v.vendor_id == vendor_id >selected</#if>
            >${v.vendor_name}</option>
        </#list>
        </select>
        <input type="button" value="提交" onclick="formSubmit()" />
    </p>
    <hr />

    <p>season_code : </p>
    <label><input name="seasonAll"
                <#list filterSeasonList as fsl>
                    <#if fsl.season_code == '-1'>
                              checked="checked"
                  <#break >
                    </#if>
                </#list>
                  type="checkbox" value="-1" />All</label>
    <#list seasonList as s>
        <label><input name="season"
                <#list filterSeasonList as fsl>
                    <#if s.season_code==fsl.season_code || fsl.season_code == '-1'>
                      checked="checked"
                    </#if>
                </#list>
                      type="checkbox" value="${s.season_code}" />${s.season_code}</label>
    </#list>

    <hr />
    <p>brand : </p>
    <label><input name="brandAll"
                <#list filterBrandList as fbl>
                    <#if  fbl.brand_id == -1>
                              checked="checked"
                              <#break >
                    </#if>
                </#list>
                  type="checkbox" value="-1" />All</label>
    <#--<#list brandList as b>
        <label><input name="brand"
                    <#list filterBrandList as fbl>
                        <#if b.brand_id == fbl.brand_id || fbl.brand_id == -1>
                              checked="checked"
                        </#if>
                    </#list>
                      type="checkbox" value="${b.brand_id}" />${b.english_name}</label>
    </#list>-->
    <#list brandList as b>
        <#list filterBrandList as fbl>
            <#if b.brand_id == fbl.brand_id || fbl.brand_id == -1>
        <label><input name="brand" checked="checked" type="checkbox" value="${b.brand_id}" />${b.english_name}</label>
            </#if>
        </#list>
    </#list>

<#list brandList as b>
    <#assign flag="false"/>
    <#list filterBrandList as fbl>
        <#if b.brand_id == fbl.brand_id || fbl.brand_id == -1>
            <#assign flag="true"/>
        </#if>
    </#list>
    <#if flag == "false">
        <label><input name="brand" type="checkbox" value="${b.brand_id}" />${b.english_name}</label>
    </#if>
</#list>

    <p></p>
</form>
</body>
<script>

    $("#selUser").change(function(){
        window.location.href="/product/filter/index?vendor_id="+$(this).val();
    });

    $("input[name='seasonAll']").change(function(){
       if($("input[name='seasonAll']").is(':checked')) {
           $("input[name='season']").each(function() {
               this.checked = true;
           });
       } else {
           $("input[name='season']").each(function() {
               this.checked = false;
           });
       }
    });

    $("input[name='brandAll']").change(function(){
        if($("input[name='brandAll']").is(':checked')) {
            $("input[name='brand']").each(function() {
                this.checked = true;
            });
        } else {
            $("input[name='brand']").each(function() {
                this.checked = false;
            });
        }
    });

    $("input[name='brand']").change(function(){
        if($(this).is(':checked')) {
            /*$("input[name='brandAll']").each(function() {
                this.checked = true;
            });*/
        } else {
            $("input[name='brandAll']").each(function() {
                this.checked = false;
            });
        }
    });

    $("input[name='season']").change(function(){
        if($(this).is(':checked')) {
            /*$("input[name='seasonAll']").each(function() {
                this.checked = true;
            });*/
        } else {
            $("input[name='seasonAll']").each(function() {
                this.checked = false;
            });
        }
    });


    function formSubmit()
    {

        var vendor_id = $("#selUser").val();

        var seasons = '';
        $("input[name='seasonAll']").each(function() {
            if(this.checked) {
                console.log($(this).val());
                seasons = seasons + $(this).val()+',';
            }
        });

        $("input[name='season']").each(function() {
            if(this.checked) {
                console.log($(this).val());
                seasons = seasons + $(this).val()+',';
            }
        });

        var brands = '';
        $("input[name='brandAll']").each(function() {
            if(this.checked) {
                console.log($(this).val());
                brands = brands + $(this).val()+'###';
            }
        });

        $("input[name='brand']").each(function() {
            if(this.checked) {
                console.log($(this).val());
                brands = brands + $(this).val()+'###';
            }
        });

        var fromData = "vendor_id="+vendor_id+"&"+"seasons="+seasons+"&brands="+brands;
        $.ajax({
            cache: false,
            type: "POST",
            url:"/product/filter/submit",
            data:fromData,
            async: false,
            error: function(request) {
                alert("Connection error:"+request.error);
            },
            success: function(data) {
                alert("success");
                window.location.reload();
            }
        });


    }

</script>
</html>
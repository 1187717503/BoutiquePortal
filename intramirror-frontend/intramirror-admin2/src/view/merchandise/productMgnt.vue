<template>
    <div>
        <div class="product-filter">
            <div class="product-filter-condition .box-row">
                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>
                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>
                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>
                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>
                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>

                <div class="product-filter-condition-input-field .input-field"> 
                    <multiselect v-model="value" :options="options"></multiselect>
                </div>
                
            </div>
            <div class="product-filter-button"></div>
        </div>
        <div class="product-table">
            <div>table</div>
        </div>

    </div>
</template>
<script>
    import Multiselect from 'vue-multiselect'
    


    export default {
        components: { Multiselect },
        data () {
            return {
                value: null,
                options: ['list', 'of', 'options']
            }
        }
    }

</script>
<style lang="less">
    @import "../../assets/css/vuetify.css";
    @import "../../../node_modules/vue-multiselect/dist/vue-multiselect.min.css";
    .product-filter {
        display: flex;
        color: rgba(0, 0, 0, 0.38);
        position: relative;
        z-index: 1;
        height: 200px;
            &-condition{
                display: flex;
                &-input-field{
                    width:200px;
                }

            }
            &-button{

            }
    }
    .box-row {
        flex-direction: row;
    }
    .input-field {
        width: 267px;
        margin: 0;
        input.select-dropdown {
            border-bottom: 2px solid #871B55;
            margin: 0;
        }
        input.validate {
            border-bottom: 2px solid #871B55;
            margin: 0;
            &:hover {
                box-shadow: none;
                border-bottom: 2px solid #871B55;
            }
            &:focus {
                box-shadow: none;
                border-bottom: 2px solid #871B55;
            }
        }
    }
</style>
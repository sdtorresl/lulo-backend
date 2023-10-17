package co.innovaciones.lulo.model


enum class UsersRole {

    ADMIN,
    CUSTOMER,
    BUSINESS_OWNER;


    class Fields {


        companion object {

            const val ADMIN = "ADMIN"

            const val CUSTOMER = "CUSTOMER"

            const val BUSINESS_OWNER = "BUSINESS_OWNER"

        }

    }

}

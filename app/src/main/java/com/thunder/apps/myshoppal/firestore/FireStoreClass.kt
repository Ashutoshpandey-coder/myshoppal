package com.thunder.apps.myshoppal.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thunder.apps.myshoppal.activities.*
import com.thunder.apps.myshoppal.activities.ui.fragments.DashboardFragment
import com.thunder.apps.myshoppal.activities.ui.fragments.OrdersFragment
import com.thunder.apps.myshoppal.activities.ui.fragments.ProductsFragment
import com.thunder.apps.myshoppal.activities.ui.fragments.SoldProductsFragment
import com.thunder.apps.myshoppal.model.*
import com.thunder.apps.myshoppal.utils.Constants

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, user: User) {
        mFireStore.collection(Constants.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, exception.message.toString())
            }

    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }


    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {

                    val sharedPref = activity.getSharedPreferences(
                        Constants.MY_SHOP_PAL_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstName} ${user.lastName}"
                    )
                    editor.apply()

                    when (activity) {
                        is LoginActivity -> activity.userLoggedInSuccess(user)
                        is RegisterActivity -> activity.userLoggedInSuccess(user)
                        is SettingsActivity -> activity.userDetailSuccess(user)
                    }
                }

            }.addOnFailureListener { exception ->
                when (activity) {
                    is LoginActivity -> activity.hideProgressDialog()
                    is RegisterActivity -> activity.hideProgressDialog()
                    is SettingsActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, exception.message.toString())
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> activity.userProfileUpdateSuccess()
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while updating profile", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val shrf: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType
                    + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileUri)
        )

        shrf.putFile(imageFileUri!!)
            .addOnSuccessListener { snapShot ->
                Log.e("Firebase Image Url", snapShot.metadata!!.reference!!.downloadUrl.toString())
                snapShot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Image Url", uri.toString())

                        when (activity) {
                            is UserProfileActivity -> activity.imageUploadSuccess(uri.toString())
                            is AddProductActivity -> activity.imageUploadSuccess(uri.toString())
                        }
                    }
            }.addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> activity.hideProgressDialog()
                    is AddProductActivity -> activity.hideProgressDialog()
                }
                Log.e("Error while uploading", "Error while uploading image to db", e)
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productDetails: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Toast.makeText(activity, "Error while adding product", Toast.LENGTH_SHORT).show()
                Log.e("Error in adding prod ::", e.message.toString())
            }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i("Product List ::", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> fragment.successProductListFromFirestore(productList)
                }
            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ProductsFragment -> fragment.hideProgressDialog()
                }
                Log.e(fragment.javaClass.simpleName, "Error while getting Product list", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val product = document.toObject(Product::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, e.message.toString())
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, e.message.toString())
                fragment.hideProgressDialog()
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(fragment.javaClass.simpleName, document.documents.toString())

                val productList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id

                    productList.add(product)
                }
                fragment.successDashboardItemList(productList)

            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e("Error :: ", e.message.toString())
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, e.message.toString())
                activity.hideProgressDialog()
            }
    }

    fun checkIfItemsExistsInCart(activity: ProductDetailsActivity, productID: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, e.message.toString())
                activity.hideProgressDialog()

            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val cartList: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)
                    if (cartItem != null) {
                        cartItem.id = i.id
                        cartList.add(cartItem)
                    }
                    when (activity) {
                        is CartListActivity -> activity.successCartItemList(cartList)
                        is CheckoutActivity -> activity.successCartItemsList(cartList)
                    }

                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> activity.hideProgressDialog()
                    is CheckoutActivity -> activity.hideProgressDialog()
                }
                Log.e("Error", "Error while getting cart List", e)
            }
    }

    fun getAllProductList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                val productList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id

                    productList.add(product)
                }
                when (activity) {
                    is CheckoutActivity -> activity.successProductListFromFirestore(productList)
                    is CartListActivity -> activity.successProductListFromFireStore(productList)
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> activity.hideProgressDialog()
                    is CartListActivity -> activity.hideProgressDialog()
                }
                Log.e("Error :: ", e.message.toString())
            }
    }

    fun removeItemFromCart(context: Context, card_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(card_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemoveSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e("Error", "Error while removing the cart item", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, hashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(hashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e("Error", "Error while updating the cart item", e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while adding address", e)
            }
    }

    fun getAddressList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val addressModel = i.toObject(Address::class.java)
                    if (addressModel != null) {
                        addressModel.id = i.id

                        addressList.add(addressModel)
                    }
                }
                activity.setUpAddressInUI(addressList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while getting address list", e)
            }
    }

    fun updateAddressSuccess(
        activity: AddEditAddressActivity,
        addressInfo: Address,
        addressId: String
    ) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while updating address", e)

            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while deleting address.", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, orderDetails: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(orderDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while placing order in fireStore", e)
            }
    }

    //We have to update details after buying the products such as stock quantity and clearing the cart
    //we add orders here so that we update the order on both side who bought it and who sold it for 3rd argument
    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        //This fireStore batch allow us to do multiple things at a time
        val writeBatch = mFireStore.batch()
        for (cartItem in cartList) {

                   val productHashMap = HashMap<String,Any>()
                   //update stock quantity
                   productHashMap[Constants.STOCK_QUANTITY] =
                       (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

                   //make a document reference to update products
                   val documentReference = mFireStore.collection(Constants.PRODUCTS)
                       .document(cartItem.product_id)
                   //update the hashMap with document reference
                   writeBatch.update(documentReference,productHashMap)

            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_dateTime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address

            )
            val documentReferences = mFireStore.collection(Constants.SOLD_PRODUCT)
                .document(cartItem.product_id)
            writeBatch.set(documentReferences,soldProduct)
        }
        //Clear the cartList also
        for (cartItem in cartList) {

            //create a document reference
            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            //this is the benefit of using batch we can do multiple things at a time.
            writeBatch.delete(documentReference)
        }
        //after using write batch such as updating something deleting something
        //we have to commit it .
        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Error", "Error while updating details after buying the product.", e)
            }
    }

    //get the order list for order fragments
    fun getMyOrderList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()
                for (i in document.documents) {
                    val orderDetails = i.toObject(Order::class.java)
                    if (orderDetails != null) {
                        orderDetails.id = i.id
                        list.add(orderDetails)
                    }
                }
                fragment.populateOrdersListInUI(list)


            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e("Error", "Error while getting my order list", e)
            }
    }
    fun getSoldProductsList(fragment : SoldProductsFragment){
        mFireStore.collection(Constants.SOLD_PRODUCT)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                val list : ArrayList<SoldProduct> = ArrayList()

                for (i in document.documents){
                    val soldProductItem = i.toObject(SoldProduct::class.java)
                    if (soldProductItem != null){
                        soldProductItem.id = i.id
                        list.add(soldProductItem)
                    }
                }
                fragment.successSoldProductsList(list)

            }
            .addOnFailureListener {
                e->
                fragment.hideProgressDialog()
                Log.e("Error","Error while getting sold products list.",e)
            }
    }
}
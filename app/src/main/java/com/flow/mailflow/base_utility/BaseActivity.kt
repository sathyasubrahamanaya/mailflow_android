package com.flow.mailflow.base_utility

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flow.mailflow.R
import com.flow.mailflow.utils.Utils.observeOnce
import com.flow.mailflow.utils.Utils.timberCall
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {
    /**
     * this is ViewModel that bound with BaseActivity please do not edit this
     * */
    var dialogBase: Dialog? = null
    var dialogBaseInternet: androidx.appcompat.app.AlertDialog? = null

    private val viewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val networkStatusTracker = NetworkStatusTracker(this@BaseActivity)
                return NetworkStatusViewModel(networkStatusTracker) as T
            }
        })[NetworkStatusViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.observe(this) {
            networkAvailable = when (it) {
                is MyState.Fetched -> {
                    true
                }

                is MyState.Error -> {
                    false
                }

                else -> {
                    false
                }
            }
        }
    }


    @SuppressLint("InflateParams")
    fun BaseActivity.showLoading(show: Boolean, loadUiType: Int = 0) {
        try {
            if (dialogBase == null) {
                dialogBase = Dialog(this, R.style.LoadingDialogWithStatusBarColor)
                val mInflater: LayoutInflater = LayoutInflater.from(this)
                val mView: View = if (loadUiType == 0) mInflater.inflate(
                    R.layout.loading_dialogue, null
                ) else mInflater.inflate(R.layout.loading_dialogue, null)
                dialogBase?.setContentView(mView)
                dialogBase?.setCancelable(loadUiType == 0)
            }

            if (show) dialogBase?.show()
            else dialogBase?.dismiss()

        } catch (e: java.lang.Exception) {
            timberCall(this@BaseActivity,"loadingDialogException", e.toString())
        }

    }

    private var networkAvailable: Boolean = true

    /**
     * @param [context] context of activity
     * @parm [message] message to be passed
     * @see [NetworkStatusTracker]
     * if internet is available and error occurs error will display as toast
     * else "Network Error:Internet not available" will display as toast
     * */
    fun BaseActivity.toastError(
        context: Context, message: String?, toastTime: Int = Toast.LENGTH_SHORT
    ) {
        Timber.tag("BaseActivityToastError").e("this is called")
        if (networkAvailable) Toast.makeText(context, message ?: EMPTY_STRING, toastTime).show()
        else Toast.makeText(context, "Network Error:Internet not available", toastTime).show()
    }


    /**
     *  This extensions helps yo to call API under Internet Observer
     *@hide
     * @param context pass this context to create custom alert dialogs when internet not available
     * @param lifecycleOwner to get observe the networks state with respect to Activity/fragment
     *@param ApiCall block to execute under internet observer
     * @param observeOnce if you need observe internet changes at once then you can set true default false
     *  * ## please pass one Api at a time **
     *   #### Don't pass this with pagination use pagination library with internet observer implementation **
     */
    fun <T> BaseActivity.withSafeApiCall(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        observeOnce: Boolean = false,
        ApiCall: () -> T
    ) {
        Timber.tag("BeforeApiCall").e("calling here")
        var networkAvailable = viewModel.state.value is MyState.Fetched

        viewModel.state.apply {
            if (observeOnce) observeOnce(lifecycleOwner) {
                when (it) {
                    is MyState.Fetched -> {
                        networkAvailable = true
                        dialogBaseInternet?.dismiss()
                        ApiCall()
                    }

                    is MyState.Error -> {
                        networkAvailable = false

                    }
                }
            }
            else {
                observe(lifecycleOwner) {
                    when (it) {
                        is MyState.Fetched -> {
                            networkAvailable = true
                            dialogBaseInternet?.dismiss()

                            ApiCall()


                        }

                        is MyState.Error -> {
                            networkAvailable = false

                        }
                    }
                }
            }
        }

        Timber.tag("networkAvailable").e(networkAvailable.toString())
        if (!networkAvailable) showApiRetryAlert(
            "No Internet Connection",
            "we can not detect any internet connectivity. Please check your internet connection and try again",
            context,
            ApiCall
        )
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            dialogBaseInternet?.dismiss()
            dialogBaseInternet = null
        }


    }

    fun <T> showApiRetryAlert(
        header: String,
        message: String,
        context: Context,
        retryCall: () -> T,


        ) {
        try {

            val builder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setTitle(header)
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton("OK", fun(dialog: DialogInterface, _: Int) {
                retryCall.invoke()
                dialog.dismiss()
            })
            dialogBaseInternet = builder.show()
            dialogBaseInternet?.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            dialogBaseInternet?.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        } catch (e: Exception) {
            timberCall(this,"API => ShowApiRetryAlert: ", e.message.toString())

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (dialogBase != null) dialogBase?.dismiss()
        dialogBase = null
        viewModel.state.removeObserver { }

    }

    companion object {
        const val EMPTY_STRING: String = "Internal Error"
    }

}
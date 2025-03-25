package com.flow.mailflow.api

data class ApiState<out T>(var status: Status,val response:  T?,val message:String?,val errorCode: Int){
    companion object{
        // In case of Success,set status as
        // Success and data as the response
        fun <T>success(data: T?):ApiState<T>{
            return ApiState(Status.SUCCESS,data,null,0)
        }

        // In case of failure ,set state to Error ,
        // add the error message,set data to null
        fun <T>error(code: Int,msg: String?):ApiState<T>{
            return ApiState(Status.ERROR,null,msg,code)
        }

        // When the call is loading set the state
        // as Loading and rest as null
        fun <T>loading():ApiState<T>{
            return ApiState(Status.LOADING,null,null, errorCode = -1)
        }

        fun <T>completed(code: Int,msg: String?):ApiState<T>{
            return ApiState(Status.COMPLETED,null,"API Completed",code)
        }
    }
}


enum class Status{
    SUCCESS,
    ERROR,
    LOADING,
    COMPLETED
}

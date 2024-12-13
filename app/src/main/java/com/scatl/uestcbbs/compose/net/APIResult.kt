package com.scatl.uestcbbs.compose.net

interface APIResult<T, F : APIResult.Failure<T>> {
    interface Failure<T>

    val isSuccess: Boolean
    val isFailure: Boolean get() = isSuccess.not()
    val data: T?
    val failure: F?
}

inline fun <T, F, R : APIResult<T, F>> R.onSuccess(successBlock: (data: T?) -> Unit): R {
    if (isSuccess) {
        successBlock(data)
    }
    return this
}

inline fun <T, F, R : APIResult<T, F>> R.onFailure(failureBlock: (failure: F) -> Unit): R {
    failure?.takeIf { isFailure }?.let(failureBlock)
    return this
}

inline fun <IT, IF, IR : APIResult<IT, IF>, OT, OF, OR : APIResult<OT, OF>> IR.onFailureThen(
    failureBlock: (failure: IF) -> OR
): OR? {
    return failure?.takeIf { isFailure }?.let(failureBlock)
}
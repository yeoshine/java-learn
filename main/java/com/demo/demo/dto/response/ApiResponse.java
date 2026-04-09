package com.demo.demo.dto.response;

/**
 * 通用接口返回体。
 *
 * @param <T> data 字段的泛型类型
 */
public class ApiResponse<T> {

    /**
     * 业务是否成功。
     */
    private boolean success;

    /**
     * 返回给前端的描述信息。
     */
    private String message;

    /**
     * 业务数据体。
     */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 快速构造成功返回。
     *
     * @param message 提示信息
     * @param data 返回数据
     * @param <T> 数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 快速构造失败返回。
     *
     * @param message 提示信息
     * @return ApiResponse
     */
    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

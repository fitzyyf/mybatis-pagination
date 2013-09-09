package org.mybatis.pagination.domain;

import com.google.common.base.Objects;

import java.io.Serializable;

public class Resources implements Serializable {
    /**
     *  主键,所属表字段为auth_resources.id
     */
    private String id;

    /**
     *  资源名称,所属表字段为auth_resources.name
     */
    private String name;

    /**
     *  资源类型,所属表字段为auth_resources.type
     */
    private String type;

    /**
     *  资源地址,所属表字段为auth_resources.path
     */
    private String path;

    /**
     *  资源请求,所属表字段为auth_resources.action
     */
    private String action;

    /**
     *  资源控制,所属表字段为auth_resources.controller
     */
    private String controller;

    /**
     *  状态,所属表字段为auth_resources.status
     */
    private Boolean status;

    /**
     * 序列化ID,auth_resources
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取 主键 字段:auth_resources.id
     *
     * @return auth_resources.id, 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置 主键 字段:auth_resources.id
     *
     * @param id auth_resources.id, 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取 资源名称 字段:auth_resources.name
     *
     * @return auth_resources.name, 资源名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 资源名称 字段:auth_resources.name
     *
     * @param name auth_resources.name, 资源名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取 资源类型 字段:auth_resources.type
     *
     * @return auth_resources.type, 资源类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置 资源类型 字段:auth_resources.type
     *
     * @param type auth_resources.type, 资源类型
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 获取 资源地址 字段:auth_resources.path
     *
     * @return auth_resources.path, 资源地址
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置 资源地址 字段:auth_resources.path
     *
     * @param path auth_resources.path, 资源地址
     */
    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    /**
     * 获取 资源请求 字段:auth_resources.action
     *
     * @return auth_resources.action, 资源请求
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置 资源请求 字段:auth_resources.action
     *
     * @param action auth_resources.action, 资源请求
     */
    public void setAction(String action) {
        this.action = action == null ? null : action.trim();
    }

    /**
     * 获取 资源控制 字段:auth_resources.controller
     *
     * @return auth_resources.controller, 资源控制
     */
    public String getController() {
        return controller;
    }

    /**
     * 设置 资源控制 字段:auth_resources.controller
     *
     * @param controller auth_resources.controller, 资源控制
     */
    public void setController(String controller) {
        this.controller = controller == null ? null : controller.trim();
    }

    /**
     * 获取 状态 字段:auth_resources.status
     *
     * @return auth_resources.status, 状态
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置 状态 字段:auth_resources.status
     *
     * @param status auth_resources.status, 状态
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("type", type)
                .add("path", path)
                .add("action", action)
                .add("controller", controller)
                .add("status", status)
                .toString();
    }

    /**
     * ,auth_resources
     *
     * @param that
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Resources other = (Resources) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getAction() == null ? other.getAction() == null : this.getAction().equals(other.getAction()))
            && (this.getController() == null ? other.getController() == null : this.getController().equals(other.getController()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    /**
     * ,auth_resources
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getAction() == null) ? 0 : getAction().hashCode());
        result = prime * result + ((getController() == null) ? 0 : getController().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }
}
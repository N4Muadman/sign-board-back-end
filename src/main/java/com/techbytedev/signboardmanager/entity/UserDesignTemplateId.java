package com.techbytedev.signboardmanager.entity;

import java.io.Serializable;

public class UserDesignTemplateId implements Serializable {
    private Long userDesignId;
    private Long designTemplateId;

    public UserDesignTemplateId() {}

    public UserDesignTemplateId(Long userDesignId, Long designTemplateId) {
        this.userDesignId = userDesignId;
        this.designTemplateId = designTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDesignTemplateId that = (UserDesignTemplateId) o;
        return userDesignId.equals(that.userDesignId) && designTemplateId.equals(that.designTemplateId);
    }

    @Override
    public int hashCode() {
        return 31 * userDesignId.hashCode() + designTemplateId.hashCode();
    }
}
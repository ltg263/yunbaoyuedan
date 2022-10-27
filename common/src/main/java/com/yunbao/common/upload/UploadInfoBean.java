package com.yunbao.common.upload;

import com.alibaba.fastjson.annotation.JSONField;

public class UploadInfoBean {

    public static final String CLOUD_TYPE_QINIU = "qiniu";
    public static final String CLOUD_TYPE_AWS = "aws";
    //华东:qiniu_hd 华北:qiniu_hb  华南:qiniu_hn  北美:qiniu_bm  新加坡:qiniu_xjp
    public static final String UPLOAD_QI_NIU_HD= "qiniu_hd";
    public static final String UPLOAD_QI_NIU_HB= "qiniu_hb";
    public static final String UPLOAD_QI_NIU_HN= "qiniu_hn";
    public static final String UPLOAD_QI_NIU_BM= "qiniu_bm";
    public static final String UPLOAD_QI_NIU_XJP= "qiniu_xjp";
    private QiniuInfoBean mQNInfo;
    private AWSInfoBean mAWSInfo;
    private String mCloudType;


    @JSONField(name = "qiniuInfo")
    public QiniuInfoBean getQNInfo() {
        return mQNInfo;
    }

    @JSONField(name = "qiniuInfo")
    public void setQNInfo(QiniuInfoBean QNInfo) {
        mQNInfo = QNInfo;
    }

    @JSONField(name = "awsInfo")
    public AWSInfoBean getAWSInfo() {
        return mAWSInfo;
    }

    @JSONField(name = "awsInfo")
    public void setAWSInfo(AWSInfoBean AWSInfo) {
        mAWSInfo = AWSInfo;
    }

    @JSONField(name = "cloudtype")
    public String getCloudType() {
        return mCloudType;
    }

    @JSONField(name = "cloudtype")
    public void setCloudType(String cloudType) {
        mCloudType = cloudType;
    }

    public static class QiniuInfoBean {

        private String mQNToken;
        private String mQNDomain;
        private String mQNZone;

        @JSONField(name = "qiniuToken")
        public String getQNToken() {
            return mQNToken;
        }

        @JSONField(name = "qiniuToken")
        public void setQNToken(String QNToken) {
            mQNToken = QNToken;
        }

        @JSONField(name = "qiniu_domain")
        public String getQNDomain() {
            return mQNDomain;
        }

        @JSONField(name = "qiniu_domain")
        public void setQNDomain(String QNDomain) {
            mQNDomain = QNDomain;
        }

        @JSONField(name = "qiniu_zone")
        public String getQNZone() {
            return mQNZone;
        }

        @JSONField(name = "qiniu_zone")
        public void setQNZone(String QNZone) {
            mQNZone = QNZone;
        }

    }

    public class AWSInfoBean{
        private String mAWSBucket;
        private String mAWSRegion;
        private String mAWSPoolId;


        @JSONField(name = "aws_bucket")
        public String getAWSBucket() {
            return mAWSBucket;
        }

        @JSONField(name = "aws_bucket")
        public void setAWSBucket(String AWSBucket) {
            mAWSBucket = AWSBucket;
        }

        @JSONField(name = "aws_region")
        public String getAWSRegion() {
            return mAWSRegion;
        }

        @JSONField(name = "aws_region")
        public void setAWSRegion(String AWSRegion) {
            mAWSRegion = AWSRegion;
        }

        @JSONField(name = "aws_identitypoolid")
        public String getAWSPoolId() {
            return mAWSPoolId;
        }

        @JSONField(name = "aws_identitypoolid")
        public void setAWSPoolId(String AWSPoolId) {
            mAWSPoolId = AWSPoolId;
        }
    }
}

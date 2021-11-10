package com.example.mock_api.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Create yubin.ding@casstime.com on 21/7/18.
 */

public class User implements Serializable {

	@SerializedName("userLoginId")
	private String userLoginId;
	@SerializedName("userLoginName")
	private String userLoginName;
	@SerializedName("userName")
	private String userName;
	@SerializedName("position")
	private String position;
	@SerializedName("contactNumber")
	private String contactNumber;
	@SerializedName("cellphone")
	private String cellphone;
	@SerializedName("userParentId")
	private String userParentId;
	@SerializedName("enabled")
	private Boolean enabled;
	@SerializedName("needChangePassword")
	private Boolean needChangePassword;
	@SerializedName("userParentName")
	private String userParentName;
	@SerializedName("authenticated")
	private String authenticated;
	@SerializedName("orgType")
	private String orgType;
	@SerializedName("registerCompleted")
	private Boolean registerCompleted;
	@SerializedName("entryApplied")
	private Boolean entryApplied;
	@SerializedName("appUser")
	private Boolean appUser;
	@SerializedName("joinParty")
	private Boolean joinParty;
	@SerializedName("updateTimes")
	private Integer updateTimes;
	@SerializedName("garageCompanyId")
	private String garageCompanyId;
	@SerializedName("isSimpleInquiryAllowed")
	private Boolean isSimpleInquiryAllowed;
	@SerializedName("isAuthenticated")
	private Boolean isAuthenticated;
	@SerializedName("companyName")
	private String companyName;
	@SerializedName("displayName")
	private String displayName;
	@SerializedName("address")
	private String address;
	@SerializedName("provinceGeoId")
	private String provinceGeoId;
	@SerializedName("cityGeoId")
	private String cityGeoId;
	@SerializedName("countyGeoId")
	private String countyGeoId;
	@SerializedName("provinceGeoName")
	private String provinceGeoName;
	@SerializedName("countyGeoName")
	private String countyGeoName;
	@SerializedName("cityGeoName")
	private String cityGeoName;
	@SerializedName("actualAddress")
	private String actualAddress;
	@SerializedName("companyCode")
	private String companyCode;
	@SerializedName("isShowEvaluateEntrance")
	private Boolean isShowEvaluateEntrance;
	@SerializedName("isSignOrderAgreement")
	private Boolean isSignOrderAgreement;
	@SerializedName("isShowQuoteStoreRemark")
	private Boolean isShowQuoteStoreRemark;
	@SerializedName("isShowQuoteStoreLevel")
	private Boolean isShowQuoteStoreLevel;
	@SerializedName("isSmallVersion")
	private Boolean isSmallVersion;
	@SerializedName("isVirtualNumber")
	private Boolean isVirtualNumber;
	@SerializedName("provinceCode")
	private String provinceCode;
	@SerializedName("isPersonalRight")
	private Boolean isPersonalRight;
	@SerializedName("accountId")
	private String accountId;

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getUserLoginName() {
		return userLoginName;
	}

	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getUserParentId() {
		return userParentId;
	}

	public void setUserParentId(String userParentId) {
		this.userParentId = userParentId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getNeedChangePassword() {
		return needChangePassword;
	}

	public void setNeedChangePassword(Boolean needChangePassword) {
		this.needChangePassword = needChangePassword;
	}

	public String getUserParentName() {
		return userParentName;
	}

	public void setUserParentName(String userParentName) {
		this.userParentName = userParentName;
	}

	public String getAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(String authenticated) {
		this.authenticated = authenticated;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public Boolean getRegisterCompleted() {
		return registerCompleted;
	}

	public void setRegisterCompleted(Boolean registerCompleted) {
		this.registerCompleted = registerCompleted;
	}

	public Boolean getEntryApplied() {
		return entryApplied;
	}

	public void setEntryApplied(Boolean entryApplied) {
		this.entryApplied = entryApplied;
	}

	public Boolean getAppUser() {
		return appUser;
	}

	public void setAppUser(Boolean appUser) {
		this.appUser = appUser;
	}

	public Boolean getJoinParty() {
		return joinParty;
	}

	public void setJoinParty(Boolean joinParty) {
		this.joinParty = joinParty;
	}

	public Integer getUpdateTimes() {
		return updateTimes;
	}

	public void setUpdateTimes(Integer updateTimes) {
		this.updateTimes = updateTimes;
	}

	public String getGarageCompanyId() {
		return garageCompanyId;
	}

	public void setGarageCompanyId(String garageCompanyId) {
		this.garageCompanyId = garageCompanyId;
	}

	public Boolean getIsSimpleInquiryAllowed() {
		return isSimpleInquiryAllowed;
	}

	public void setIsSimpleInquiryAllowed(Boolean isSimpleInquiryAllowed) {
		this.isSimpleInquiryAllowed = isSimpleInquiryAllowed;
	}

	public Boolean getIsAuthenticated() {
		return isAuthenticated;
	}

	public void setIsAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvinceGeoId() {
		return provinceGeoId;
	}

	public void setProvinceGeoId(String provinceGeoId) {
		this.provinceGeoId = provinceGeoId;
	}

	public String getCityGeoId() {
		return cityGeoId;
	}

	public void setCityGeoId(String cityGeoId) {
		this.cityGeoId = cityGeoId;
	}

	public String getCountyGeoId() {
		return countyGeoId;
	}

	public void setCountyGeoId(String countyGeoId) {
		this.countyGeoId = countyGeoId;
	}

	public String getProvinceGeoName() {
		return provinceGeoName;
	}

	public void setProvinceGeoName(String provinceGeoName) {
		this.provinceGeoName = provinceGeoName;
	}

	public String getCountyGeoName() {
		return countyGeoName;
	}

	public void setCountyGeoName(String countyGeoName) {
		this.countyGeoName = countyGeoName;
	}

	public String getCityGeoName() {
		return cityGeoName;
	}

	public void setCityGeoName(String cityGeoName) {
		this.cityGeoName = cityGeoName;
	}

	public String getActualAddress() {
		return actualAddress;
	}

	public void setActualAddress(String actualAddress) {
		this.actualAddress = actualAddress;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public Boolean getIsShowEvaluateEntrance() {
		return isShowEvaluateEntrance;
	}

	public void setIsShowEvaluateEntrance(Boolean isShowEvaluateEntrance) {
		this.isShowEvaluateEntrance = isShowEvaluateEntrance;
	}

	public Boolean getIsSignOrderAgreement() {
		return isSignOrderAgreement;
	}

	public void setIsSignOrderAgreement(Boolean isSignOrderAgreement) {
		this.isSignOrderAgreement = isSignOrderAgreement;
	}

	public Boolean getIsShowQuoteStoreRemark() {
		return isShowQuoteStoreRemark;
	}

	public void setIsShowQuoteStoreRemark(Boolean isShowQuoteStoreRemark) {
		this.isShowQuoteStoreRemark = isShowQuoteStoreRemark;
	}

	public Boolean getIsShowQuoteStoreLevel() {
		return isShowQuoteStoreLevel;
	}

	public void setIsShowQuoteStoreLevel(Boolean isShowQuoteStoreLevel) {
		this.isShowQuoteStoreLevel = isShowQuoteStoreLevel;
	}

	public Boolean getIsSmallVersion() {
		return isSmallVersion;
	}

	public void setIsSmallVersion(Boolean isSmallVersion) {
		this.isSmallVersion = isSmallVersion;
	}

	public Boolean getIsVirtualNumber() {
		return isVirtualNumber;
	}

	public void setIsVirtualNumber(Boolean isVirtualNumber) {
		this.isVirtualNumber = isVirtualNumber;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public Boolean getIsPersonalRight() {
		return isPersonalRight;
	}

	public void setIsPersonalRight(Boolean isPersonalRight) {
		this.isPersonalRight = isPersonalRight;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		return "User{" +
				"userLoginId='" + userLoginId + '\'' +
				", userLoginName='" + userLoginName + '\'' +
				", userName='" + userName + '\'' +
				", position='" + position + '\'' +
				", contactNumber='" + contactNumber + '\'' +
				", cellphone='" + cellphone + '\'' +
				", userParentId='" + userParentId + '\'' +
				", enabled=" + enabled +
				", needChangePassword=" + needChangePassword +
				", userParentName='" + userParentName + '\'' +
				", authenticated='" + authenticated + '\'' +
				", orgType='" + orgType + '\'' +
				", registerCompleted=" + registerCompleted +
				", entryApplied=" + entryApplied +
				", appUser=" + appUser +
				", joinParty=" + joinParty +
				", updateTimes=" + updateTimes +
				", garageCompanyId='" + garageCompanyId + '\'' +
				", isSimpleInquiryAllowed=" + isSimpleInquiryAllowed +
				", isAuthenticated=" + isAuthenticated +
				", companyName='" + companyName + '\'' +
				", displayName='" + displayName + '\'' +
				", address='" + address + '\'' +
				", provinceGeoId='" + provinceGeoId + '\'' +
				", cityGeoId='" + cityGeoId + '\'' +
				", countyGeoId='" + countyGeoId + '\'' +
				", provinceGeoName='" + provinceGeoName + '\'' +
				", countyGeoName='" + countyGeoName + '\'' +
				", cityGeoName='" + cityGeoName + '\'' +
				", actualAddress='" + actualAddress + '\'' +
				", companyCode='" + companyCode + '\'' +
				", isShowEvaluateEntrance=" + isShowEvaluateEntrance +
				", isSignOrderAgreement=" + isSignOrderAgreement +
				", isShowQuoteStoreRemark=" + isShowQuoteStoreRemark +
				", isShowQuoteStoreLevel=" + isShowQuoteStoreLevel +
				", isSmallVersion=" + isSmallVersion +
				", isVirtualNumber=" + isVirtualNumber +
				", provinceCode='" + provinceCode + '\'' +
				", isPersonalRight=" + isPersonalRight +
				", accountId='" + accountId + '\'' +
				'}';
	}
}

////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2008 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 1.50Release
// Tag = $Name: AKW1_500 $
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;


public class BikeProfileMesg extends Mesg {


   public BikeProfileMesg() {
      super(Factory.createMesg(MesgNum.BIKE_PROFILE));
   }

   public BikeProfileMesg(final Mesg mesg) {
      super(mesg);
   }


   /**
    * Get message_index field
    *
    * @return message_index
    */
   public Integer getMessageIndex() {
      return getFieldIntegerValue(254, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set message_index field
    *
    * @param messageIndex
    */
   public void setMessageIndex(Integer messageIndex) {
      setFieldValue(254, 0, messageIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get name field
    *
    * @return name
    */
   public String getName() {
      return getFieldStringValue(0, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set name field
    *
    * @param name
    */
   public void setName(String name) {
      setFieldValue(0, 0, name, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sport field
    *
    * @return sport
    */
   public Sport getSport() {
      Short value = getFieldShortValue(1, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Sport.getByValue(value);
   }

   /**
    * Set sport field
    *
    * @param sport
    */
   public void setSport(Sport sport) {
      setFieldValue(1, 0, sport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sub_sport field
    *
    * @return sub_sport
    */
   public SubSport getSubSport() {
      Short value = getFieldShortValue(2, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return SubSport.getByValue(value);
   }

   /**
    * Set sub_sport field
    *
    * @param subSport
    */
   public void setSubSport(SubSport subSport) {
      setFieldValue(2, 0, subSport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get odometer field
    * Units: m
    *
    * @return odometer
    */
   public Float getOdometer() {
      return getFieldFloatValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set odometer field
    * Units: m
    *
    * @param odometer
    */
   public void setOdometer(Float odometer) {
      setFieldValue(3, 0, odometer, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_spd_ant_id field
    *
    * @return bike_spd_ant_id
    */
   public Integer getBikeSpdAntId() {
      return getFieldIntegerValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_spd_ant_id field
    *
    * @param bikeSpdAntId
    */
   public void setBikeSpdAntId(Integer bikeSpdAntId) {
      setFieldValue(4, 0, bikeSpdAntId, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_cad_ant_id field
    *
    * @return bike_cad_ant_id
    */
   public Integer getBikeCadAntId() {
      return getFieldIntegerValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_cad_ant_id field
    *
    * @param bikeCadAntId
    */
   public void setBikeCadAntId(Integer bikeCadAntId) {
      setFieldValue(5, 0, bikeCadAntId, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_spdcad_ant_id field
    *
    * @return bike_spdcad_ant_id
    */
   public Integer getBikeSpdcadAntId() {
      return getFieldIntegerValue(6, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_spdcad_ant_id field
    *
    * @param bikeSpdcadAntId
    */
   public void setBikeSpdcadAntId(Integer bikeSpdcadAntId) {
      setFieldValue(6, 0, bikeSpdcadAntId, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_power_ant_id field
    *
    * @return bike_power_ant_id
    */
   public Integer getBikePowerAntId() {
      return getFieldIntegerValue(7, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_power_ant_id field
    *
    * @param bikePowerAntId
    */
   public void setBikePowerAntId(Integer bikePowerAntId) {
      setFieldValue(7, 0, bikePowerAntId, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get custom_wheelsize field
    * Units: m
    *
    * @return custom_wheelsize
    */
   public Float getCustomWheelsize() {
      return getFieldFloatValue(8, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set custom_wheelsize field
    * Units: m
    *
    * @param customWheelsize
    */
   public void setCustomWheelsize(Float customWheelsize) {
      setFieldValue(8, 0, customWheelsize, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get auto_wheelsize field
    * Units: m
    *
    * @return auto_wheelsize
    */
   public Float getAutoWheelsize() {
      return getFieldFloatValue(9, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set auto_wheelsize field
    * Units: m
    *
    * @param autoWheelsize
    */
   public void setAutoWheelsize(Float autoWheelsize) {
      setFieldValue(9, 0, autoWheelsize, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_weight field
    * Units: kg
    *
    * @return bike_weight
    */
   public Float getBikeWeight() {
      return getFieldFloatValue(10, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_weight field
    * Units: kg
    *
    * @param bikeWeight
    */
   public void setBikeWeight(Float bikeWeight) {
      setFieldValue(10, 0, bikeWeight, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get power_cal_factor field
    * Units: %
    *
    * @return power_cal_factor
    */
   public Float getPowerCalFactor() {
      return getFieldFloatValue(11, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set power_cal_factor field
    * Units: %
    *
    * @param powerCalFactor
    */
   public void setPowerCalFactor(Float powerCalFactor) {
      setFieldValue(11, 0, powerCalFactor, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get auto_wheel_cal field
    *
    * @return auto_wheel_cal
    */
   public Bool getAutoWheelCal() {
      Short value = getFieldShortValue(12, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set auto_wheel_cal field
    *
    * @param autoWheelCal
    */
   public void setAutoWheelCal(Bool autoWheelCal) {
      setFieldValue(12, 0, autoWheelCal.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get auto_power_zero field
    *
    * @return auto_power_zero
    */
   public Bool getAutoPowerZero() {
      Short value = getFieldShortValue(13, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set auto_power_zero field
    *
    * @param autoPowerZero
    */
   public void setAutoPowerZero(Bool autoPowerZero) {
      setFieldValue(13, 0, autoPowerZero.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get id field
    *
    * @return id
    */
   public Short getId() {
      return getFieldShortValue(14, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set id field
    *
    * @param id
    */
   public void setId(Short id) {
      setFieldValue(14, 0, id, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get spd_enabled field
    *
    * @return spd_enabled
    */
   public Bool getSpdEnabled() {
      Short value = getFieldShortValue(15, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set spd_enabled field
    *
    * @param spdEnabled
    */
   public void setSpdEnabled(Bool spdEnabled) {
      setFieldValue(15, 0, spdEnabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get cad_enabled field
    *
    * @return cad_enabled
    */
   public Bool getCadEnabled() {
      Short value = getFieldShortValue(16, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set cad_enabled field
    *
    * @param cadEnabled
    */
   public void setCadEnabled(Bool cadEnabled) {
      setFieldValue(16, 0, cadEnabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get spdcad_enabled field
    *
    * @return spdcad_enabled
    */
   public Bool getSpdcadEnabled() {
      Short value = getFieldShortValue(17, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set spdcad_enabled field
    *
    * @param spdcadEnabled
    */
   public void setSpdcadEnabled(Bool spdcadEnabled) {
      setFieldValue(17, 0, spdcadEnabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get power_enabled field
    *
    * @return power_enabled
    */
   public Bool getPowerEnabled() {
      Short value = getFieldShortValue(18, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set power_enabled field
    *
    * @param powerEnabled
    */
   public void setPowerEnabled(Bool powerEnabled) {
      setFieldValue(18, 0, powerEnabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get crank_length field
    * Units: mm
    *
    * @return crank_length
    */
   public Float getCrankLength() {
      return getFieldFloatValue(19, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set crank_length field
    * Units: mm
    *
    * @param crankLength
    */
   public void setCrankLength(Float crankLength) {
      setFieldValue(19, 0, crankLength, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get enabled field
    *
    * @return enabled
    */
   public Bool getEnabled() {
      Short value = getFieldShortValue(20, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set enabled field
    *
    * @param enabled
    */
   public void setEnabled(Bool enabled) {
      setFieldValue(20, 0, enabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_spd_ant_id_trans_type field
    *
    * @return bike_spd_ant_id_trans_type
    */
   public Short getBikeSpdAntIdTransType() {
      return getFieldShortValue(21, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_spd_ant_id_trans_type field
    *
    * @param bikeSpdAntIdTransType
    */
   public void setBikeSpdAntIdTransType(Short bikeSpdAntIdTransType) {
      setFieldValue(21, 0, bikeSpdAntIdTransType, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_cad_ant_id_trans_type field
    *
    * @return bike_cad_ant_id_trans_type
    */
   public Short getBikeCadAntIdTransType() {
      return getFieldShortValue(22, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_cad_ant_id_trans_type field
    *
    * @param bikeCadAntIdTransType
    */
   public void setBikeCadAntIdTransType(Short bikeCadAntIdTransType) {
      setFieldValue(22, 0, bikeCadAntIdTransType, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_spdcad_ant_id_trans_type field
    *
    * @return bike_spdcad_ant_id_trans_type
    */
   public Short getBikeSpdcadAntIdTransType() {
      return getFieldShortValue(23, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_spdcad_ant_id_trans_type field
    *
    * @param bikeSpdcadAntIdTransType
    */
   public void setBikeSpdcadAntIdTransType(Short bikeSpdcadAntIdTransType) {
      setFieldValue(23, 0, bikeSpdcadAntIdTransType, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get bike_power_ant_id_trans_type field
    *
    * @return bike_power_ant_id_trans_type
    */
   public Short getBikePowerAntIdTransType() {
      return getFieldShortValue(24, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set bike_power_ant_id_trans_type field
    *
    * @param bikePowerAntIdTransType
    */
   public void setBikePowerAntIdTransType(Short bikePowerAntIdTransType) {
      setFieldValue(24, 0, bikePowerAntIdTransType, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get checksum field
    *
    * @return checksum
    */
   public Short getChecksum() {
      return getFieldShortValue(252, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set checksum field
    *
    * @param checksum
    */
   public void setChecksum(Short checksum) {
      setFieldValue(252, 0, checksum, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

}

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


public class GoalMesg extends Mesg {


   public GoalMesg() {
      super(Factory.createMesg(MesgNum.GOAL));
   }

   public GoalMesg(final Mesg mesg) {
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
    * Get sport field
    *
    * @return sport
    */
   public Sport getSport() {
      Short value = getFieldShortValue(0, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
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
      setFieldValue(0, 0, sport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get sub_sport field
    *
    * @return sub_sport
    */
   public SubSport getSubSport() {
      Short value = getFieldShortValue(1, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
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
      setFieldValue(1, 0, subSport.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get start_date field
    *
    * @return start_date
    */
   public DateTime getStartDate() {
      return timestampToDateTime(getFieldLongValue(2, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set start_date field
    *
    * @param startDate
    */
   public void setStartDate(DateTime startDate) {
      setFieldValue(2, 0, startDate.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get end_date field
    *
    * @return end_date
    */
   public DateTime getEndDate() {
      return timestampToDateTime(getFieldLongValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD));
   }

   /**
    * Set end_date field
    *
    * @param endDate
    */
   public void setEndDate(DateTime endDate) {
      setFieldValue(3, 0, endDate.getTimestamp(), Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get type field
    *
    * @return type
    */
   public Goal getType() {
      Short value = getFieldShortValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Goal.getByValue(value);
   }

   /**
    * Set type field
    *
    * @param type
    */
   public void setType(Goal type) {
      setFieldValue(4, 0, type.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get value field
    *
    * @return value
    */
   public Long getValue() {
      return getFieldLongValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set value field
    *
    * @param value
    */
   public void setValue(Long value) {
      setFieldValue(5, 0, value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get repeat field
    *
    * @return repeat
    */
   public Bool getRepeat() {
      Short value = getFieldShortValue(6, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return Bool.getByValue(value);
   }

   /**
    * Set repeat field
    *
    * @param repeat
    */
   public void setRepeat(Bool repeat) {
      setFieldValue(6, 0, repeat.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get target_value field
    *
    * @return target_value
    */
   public Long getTargetValue() {
      return getFieldLongValue(7, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set target_value field
    *
    * @param targetValue
    */
   public void setTargetValue(Long targetValue) {
      setFieldValue(7, 0, targetValue, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get recurrence field
    *
    * @return recurrence
    */
   public GoalRecurrence getRecurrence() {
      Short value = getFieldShortValue(8, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
      if (value == null)
         return null;
      return GoalRecurrence.getByValue(value);
   }

   /**
    * Set recurrence field
    *
    * @param recurrence
    */
   public void setRecurrence(GoalRecurrence recurrence) {
      setFieldValue(8, 0, recurrence.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get recurrence_value field
    *
    * @return recurrence_value
    */
   public Integer getRecurrenceValue() {
      return getFieldIntegerValue(9, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set recurrence_value field
    *
    * @param recurrenceValue
    */
   public void setRecurrenceValue(Integer recurrenceValue) {
      setFieldValue(9, 0, recurrenceValue, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get enabled field
    *
    * @return enabled
    */
   public Bool getEnabled() {
      Short value = getFieldShortValue(10, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
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
      setFieldValue(10, 0, enabled.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
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

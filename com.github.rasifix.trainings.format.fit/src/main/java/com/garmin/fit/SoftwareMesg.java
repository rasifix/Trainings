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


public class SoftwareMesg extends Mesg {


   public SoftwareMesg() {
      super(Factory.createMesg(MesgNum.SOFTWARE));
   }

   public SoftwareMesg(final Mesg mesg) {
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
    * Get version field
    *
    * @return version
    */
   public Float getVersion() {
      return getFieldFloatValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set version field
    *
    * @param version
    */
   public void setVersion(Float version) {
      setFieldValue(3, 0, version, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Get part_number field
    *
    * @return part_number
    */
   public String getPartNumber() {
      return getFieldStringValue(5, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
   }

   /**
    * Set part_number field
    *
    * @param partNumber
    */
   public void setPartNumber(String partNumber) {
      setFieldValue(5, 0, partNumber, Fit.SUBFIELD_INDEX_MAIN_FIELD);
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

package edu.hm.cs.bka.swt2.helpme.service.dto;

import lombok.Data;

@Data
public class SubscriptionDto {
  UserDisplayDto observer;
  boolean writeAccess;

}

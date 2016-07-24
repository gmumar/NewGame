package com.climber.models;

import com.backendless.Backendless;

public class Challenges
{
  private Integer challengeReward;
  private String objectId;
  private java.util.Date created;
  private Double bestTime;
  private String sourceUser;
  private String targetUser;
  private java.util.Date updated;
  private String ownerId;
  private String challenge;

  public Integer getChallengeReward()
  {
    return this.challengeReward;
  }

  public String getObjectId()
  {
    return this.objectId;
  }

  public java.util.Date getCreated()
  {
    return this.created;
  }

  public Double getBestTime()
  {
    return this.bestTime;
  }

  public String getSourceUser()
  {
    return this.sourceUser;
  }

  public String getTargetUser()
  {
    return this.targetUser;
  }

  public java.util.Date getUpdated()
  {
    return this.updated;
  }

  public String getOwnerId()
  {
    return this.ownerId;
  }

  public String getChallenge()
  {
    return this.challenge;
  }


  public void setChallengeReward( Integer challengeReward )
  {
    this.challengeReward = challengeReward;
  }

  public void setObjectId( String objectId )
  {
    this.objectId = objectId;
  }

  public void setCreated( java.util.Date created )
  {
    this.created = created;
  }

  public void setBestTime( Double bestTime )
  {
    this.bestTime = bestTime;
  }

  public void setSourceUser( String sourceUser )
  {
    this.sourceUser = sourceUser;
  }

  public void setTargetUser( String targetUser )
  {
    this.targetUser = targetUser;
  }

  public void setUpdated( java.util.Date updated )
  {
    this.updated = updated;
  }

  public void setOwnerId( String ownerId )
  {
    this.ownerId = ownerId;
  }

  public void setChallenge( String challenge )
  {
    this.challenge = challenge;
  }

  public Challenges save()
  {
    return Backendless.Data.of( Challenges.class ).save( this );
  }

  public Long remove()
  {
    return Backendless.Data.of( Challenges.class ).remove( this );
  }

  public static Challenges findById( String id )
  {
    return Backendless.Data.of( Challenges.class ).findById( id );
  }

  public static Challenges findFirst()
  {
    return Backendless.Data.of( Challenges.class ).findFirst();
  }

  public static Challenges findLast()
  {
    return Backendless.Data.of( Challenges.class ).findLast();
  }
}
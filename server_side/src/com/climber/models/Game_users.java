package com.climber.models;

import com.backendless.Backendless;

public class Game_users
{
  private Integer moneyDelta;
  private String userName;
  private java.util.Date created;
  private java.util.Date updated;
  private java.util.Date lastActivity;
  private String ownerId;
  private String objectId;

  public Integer getMoneyDelta()
  {
    return this.moneyDelta;
  }

  public String getUserName()
  {
    return this.userName;
  }

  public java.util.Date getCreated()
  {
    return this.created;
  }

  public java.util.Date getUpdated()
  {
    return this.updated;
  }

  public java.util.Date getLastActivity()
  {
    return this.lastActivity;
  }

  public String getOwnerId()
  {
    return this.ownerId;
  }

  public String getObjectId()
  {
    return this.objectId;
  }


  public void setMoneyDelta( Integer moneyDelta )
  {
    this.moneyDelta = moneyDelta;
  }

  public void setUserName( String userName )
  {
    this.userName = userName;
  }

  public void setCreated( java.util.Date created )
  {
    this.created = created;
  }

  public void setUpdated( java.util.Date updated )
  {
    this.updated = updated;
  }

  public void setLastActivity( java.util.Date lastActivity )
  {
    this.lastActivity = lastActivity;
  }

  public void setOwnerId( String ownerId )
  {
    this.ownerId = ownerId;
  }

  public void setObjectId( String objectId )
  {
    this.objectId = objectId;
  }

  public Game_users save()
  {
    return Backendless.Data.of( Game_users.class ).save( this );
  }

  public Long remove()
  {
    return Backendless.Data.of( Game_users.class ).remove( this );
  }

  public static Game_users findById( String id )
  {
    return Backendless.Data.of( Game_users.class ).findById( id );
  }

  public static Game_users findFirst()
  {
    return Backendless.Data.of( Game_users.class ).findFirst();
  }

  public static Game_users findLast()
  {
    return Backendless.Data.of( Game_users.class ).findLast();
  }
}
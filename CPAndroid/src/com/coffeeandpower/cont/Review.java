package com.coffeeandpower.cont;

public class Review
{

	private int id;
	private String author;
	private String title;
	private String type;
	private String createTime;
	private String skill;
	private String rating;
	private String isLove;
	private String tipAmount;
	private String review;
	private String ratingImage;
	private String relativeTime;

	public Review(int id, String author, String title, String type, String createTime, String skill, String rating,
			String isLove, String tipAmount, String review, String ratingImage, String relativeTime)
	{

		this.id = id;
		this.author = author;
		this.title = title;
		this.type = type;
		this.createTime = createTime;
		this.skill = skill;
		this.rating = rating;
		this.isLove = isLove;
		this.tipAmount = tipAmount;
		this.review = review;
		this.ratingImage = ratingImage;
		this.relativeTime = relativeTime;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public String getSkill()
	{
		return skill;
	}

	public void setSkill(String skill)
	{
		this.skill = skill;
	}

	public String getRating()
	{
		return rating;
	}

	public void setRating(String rating)
	{
		this.rating = rating;
	}

	public String getIsLove()
	{
		return isLove;
	}

	public void setIsLove(String isLove)
	{
		this.isLove = isLove;
	}

	public String getTipAmount()
	{
		return tipAmount;
	}

	public void setTipAmount(String tipAmount)
	{
		this.tipAmount = tipAmount;
	}

	public String getReview()
	{
		return review;
	}

	public void setReview(String review)
	{
		this.review = review;
	}

	public String getRatingImage()
	{
		return ratingImage;
	}

	public void setRatingImage(String ratingImage)
	{
		this.ratingImage = ratingImage;
	}

	public String getRelativeTime()
	{
		return relativeTime;
	}

	public void setRelativeTime(String relativeTime)
	{
		this.relativeTime = relativeTime;
	}

}

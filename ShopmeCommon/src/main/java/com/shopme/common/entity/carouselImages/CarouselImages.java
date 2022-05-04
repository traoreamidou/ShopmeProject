package com.shopme.common.entity.carouselImages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.IdBasedEntity;

@Entity
@Table(name = "carousel_images")
public class CarouselImages extends IdBasedEntity {

	@Column(nullable = false)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public String getImagePath() {
		return "/images/carousel_images/" + this.name;
	}
}

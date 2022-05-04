package com.shopme.carouselImageSlider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.carouselImages.CarouselImages;

@Service
public class CarouselImagesServices {

	@Autowired
	private CarouselImagesRepository repo;
	
	public List<CarouselImages> findAll() {
		return (List<CarouselImages>)repo.findAll();
	}
}

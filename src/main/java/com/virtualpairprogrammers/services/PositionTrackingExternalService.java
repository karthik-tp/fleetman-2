package com.virtualpairprogrammers.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.virtualpairprogrammers.controllers.Position;
import com.virtualpairprogrammers.data.VehicleRepository;
import com.virtualpairprogrammers.domain.Vehicle;

@Service
public class PositionTrackingExternalService
{
	@Autowired
	private LoadBalancerClient balancer;

	@Autowired
	private VehicleRepository repository;

	@Autowired
	private RemotePositionMicroserviceCalls remoteService;

	@HystrixCommand(fallbackMethod="handleExternalServiceDown")
	public Position getLatestPositionForVehicleFromRemoteMicroservice(String name)
	{
		Position response = remoteService.getLatestPostionForVehicle(name);
		response.setUpToDate(true);

		return response;
	}


	public Position handleExternalServiceDown(String name)
	{
		// Read the last known position for this vehicle
		Position position = new Position();
		Vehicle vehicle = repository.findByName(name);
		position.setLat(vehicle.getLat());
		position.setLongitude(vehicle.getLongitude());
		position.setTimestamp(vehicle.getLastRecordedPosition());
		position.setUpToDate(false);
		return position;
	}

}

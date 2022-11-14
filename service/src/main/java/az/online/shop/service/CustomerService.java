package az.online.shop.service;

import az.online.shop.dao.CustomerRepository;
import az.online.shop.dto.CustomerCreateEditDto;
import az.online.shop.dto.CustomerReadDto;
import az.online.shop.entity.Customer;
import az.online.shop.mapper.CustomerCreateEditMapper;
import az.online.shop.mapper.CustomerReadMapper;
import az.online.shop.model.Status;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerReadMapper customerReadMapper;
    private final CustomerCreateEditMapper customerCreateEditMapper;

    @Transactional
    public CustomerReadDto create(CustomerCreateEditDto customerDto) {
        return Optional.of(customerDto)
                .map(customerCreateEditMapper::map)
                .map(customerRepository::save)
                .map(customerReadMapper::map)
                .orElseThrow();
    }

    @Transactional
    public Optional<CustomerReadDto> findById(Integer id) {
        return customerRepository.findById(id)
                .map(customerReadMapper::map);
    }

    @Transactional
    public Optional<CustomerReadDto> update(Integer id, CustomerCreateEditDto customerDto) {
        return customerRepository.findById(id)
                .map(entity -> customerCreateEditMapper.map(customerDto, entity))
                .map(customerRepository::saveAndFlush)
                .map(customerReadMapper::map);
    }

    @Transactional
    public boolean delete(Integer id) {
        return customerRepository.findById(id)
                .map(entity -> {
                    customerRepository.delete(entity);
                    customerRepository.flush();
                    return true;
                }).orElse(false);
    }

    public List<CustomerReadDto> getAllCustomersByOrderStatus(Status status) {
        List<Customer> customers = customerRepository.findAllByStatus(status);
        return customerReadMapper.mapFrom(customers);
    }

    public List<CustomerReadDto> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(customerReadMapper::map)
                .toList();
    }
}
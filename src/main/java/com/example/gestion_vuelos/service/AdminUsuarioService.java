package com.example.gestion_vuelos.service;

import org.springframework.transaction.annotation.Transactional;
import com.example.gestion_vuelos.dto.PasajeroInfoBasicaDTO;
import com.example.gestion_vuelos.dto.UsuarioResponseDTO;
import com.example.gestion_vuelos.model.Reserva;
import com.example.gestion_vuelos.model.Usuario;
import com.example.gestion_vuelos.repository.PasajeroRepository;
import com.example.gestion_vuelos.repository.ReservaRepository;
import com.example.gestion_vuelos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Desactivar usuario
    public UsuarioResponseDTO toggleUsuarioEstado(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(!usuario.getActivo());
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertToResponseDTO(updatedUsuario);
    }

    public UsuarioResponseDTO obtenerUsuarioPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToResponseDTO(usuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // No permitir eliminar administradores
        if ("ADMIN".equals(usuario.getRol())) {
            throw new RuntimeException("No se puede eliminar un usuario administrador");
        }

        // Verificar reservas activas
        if (usuario.getPasajero() != null) {
            List<Reserva> reservas = reservaRepository.findByPasajeroIdPasajero(usuario.getPasajero().getIdPasajero());
            boolean tieneReservasActivas = reservas.stream()
                    .anyMatch(r -> !"CANCELADA".equals(r.getEstadoReserva()));

            if (tieneReservasActivas) {
                throw new RuntimeException("No se puede eliminar el usuario porque tiene reservas activas");
            }

            // Eliminar el pasajero primero
            pasajeroRepository.delete(usuario.getPasajero());
        }

        // Eliminar el usuario
        usuarioRepository.delete(usuario);
    }

    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setActivo(usuario.getActivo());

        // Si el usuario tiene un pasajero asociado, incluir su información básica
        if (usuario.getPasajero() != null) {
            PasajeroInfoBasicaDTO pasajeroInfo = new PasajeroInfoBasicaDTO();
            pasajeroInfo.setIdPasajero(usuario.getPasajero().getIdPasajero());
            pasajeroInfo.setNombreCompleto(usuario.getPasajero().getNombreCompleto());
            pasajeroInfo.setNumPasaporte(usuario.getPasajero().getNumPasaporte());
            pasajeroInfo.setNacionalidad(usuario.getPasajero().getNacionalidad());
            pasajeroInfo.setNumTelefono(usuario.getPasajero().getNumTelefono());
            dto.setPasajero(pasajeroInfo);
        }

        return dto;
    }
}

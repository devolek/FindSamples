import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Type {
    private String name;
    private List<Art> arts;
}

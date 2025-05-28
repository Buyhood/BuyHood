package api.buyhood.domain.product.entity;

import api.buyhood.global.common.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
	name = "categories",
	uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "name"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Min(0)
	private int depth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> children = new ArrayList<>();

	@Builder
	public Category(String name, int depth, Category parent, List<Category> children) {
		this.name = name;
		this.depth = depth;
		this.parent = parent;
		this.children = children;
	}

	public void addChildCategory(Category child) {
		this.children.add(child);
		child.parent = this;
	}

	public static List<Long> extractLowestCategoryIds(Category category) {
		if (category.getChildren() == null || category.getChildren().isEmpty()) {
			return List.of(category.getId());
		}

		return category.getChildren()
			.stream()
			.map(Category::extractLowestCategoryIds)
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}
}
